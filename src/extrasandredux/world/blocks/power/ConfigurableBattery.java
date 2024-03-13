package extrasandredux.world.blocks.power;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import extrasandredux.util.*;
import extrasandredux.world.consumers.*;
import extrasandredux.world.draw.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.power.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

@SuppressWarnings("unchecked")
public class ConfigurableBattery extends Battery{
    protected static float powerCapacitySetting;

    public float initialPowerCapacity = 1000f;

    public ConfigurableBattery(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.power);

        configurable = saveConfig = true;
        update = true;

        drawer = new DrawMulti(new DrawDefault(), new DrawStrobePower(), new DrawRegion("-top"), new DrawStrobe());

        consumeBufferedPowerDynamic((ConfigurableBatteryBuild build) -> build.powerCapacity);

        config(Float.class, (ConfigurableBatteryBuild build, Float f) -> {
            float amount = build.powerCapacity * build.power.status;
            build.powerCapacity = f;
            build.block.consPower.update(build);
            build.power.status = Math.min(amount, build.powerCapacity) / build.powerCapacity;
        });
        config(Boolean.class, (ConfigurableBatteryBuild build, Boolean ignored) -> build.power.status = 0);
    }

    @Override
    public void setBars(){
        super.setBars();

        removeBar("power");
        ConsumeBufferedPowerDynamic dynBufferedPower = (ConsumeBufferedPowerDynamic)consPower;
        addBar("power", entity -> new Bar(
            () -> {
                float capacity = dynBufferedPower.getPowerCapacity(entity);
                return Core.bundle.format("bar.poweramount", Float.isNaN(entity.power.status * capacity) ? "<ERROR>" : ESRUtls.round(entity.power.status * capacity)) + "/" + ESRUtls.round(capacity);
            },
            () -> Pal.powerBar,
            () -> Mathf.zero(consPower.requestedPower(entity)) && entity.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f ? 1f : entity.power.status)
        );
    }

    public <T extends Building> ConsumePower consumeBufferedPowerDynamic(Floatf<T> usage){
        return consume(new ConsumeBufferedPowerDynamic((Floatf<Building>)usage));
    }

    public class ConfigurableBatteryBuild extends BatteryBuild{
        public float powerCapacity = initialPowerCapacity;

        @Override
        public void buildConfiguration(Table table){
            powerCapacitySetting = powerCapacity;
            table.table(Styles.black5, t -> {
                t.margin(6f);
                t.field(String.valueOf(powerCapacitySetting), text -> {
                    powerCapacitySetting = Strings.parseFloat(text);
                }).width(120).valid(text -> Strings.canParseFloat(text) && Strings.parseFloat(text) > 0).get().setFilter(TextFieldFilter.floatsOnly);
                t.add(ESRUtls.statUnitName(StatUnit.powerUnits)).left();
                t.button(Icon.save, () -> configure(powerCapacitySetting)).padLeft(6);
                t.button(Icon.trash, () -> configure(false)).tooltip("@esr-storage.delete-contents");
            });
        }

        @Override
        public Object config(){
            return powerCapacity;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(powerCapacity);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            powerCapacity = read.f();
        }
    }
}
