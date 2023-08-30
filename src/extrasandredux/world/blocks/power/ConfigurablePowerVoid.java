package extrasandredux.world.blocks.power;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import extrasandredux.graphics.*;
import extrasandredux.util.*;
import extrasandredux.world.blocks.heat.ConfigurableHeatSource.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

public class ConfigurablePowerVoid extends PowerBlock{
    public float defaultPowerConsumption = 1000f;
    public TextureRegion colorRegion;

    public ConfigurablePowerVoid(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.power);

        outputsPower = false;
        consumesPower = true;
        configurable = saveConfig = true;
        canOverdrive = false;

        consumePowerDynamic((ConfigurablePowerVoidBuild build) -> build.powerConsumption / 60f);

        config(Float.class, (ConfigurablePowerVoidBuild build, Float f) -> build.powerConsumption = f);
    }

    @Override
    public void load(){
        super.load();
        colorRegion = Core.atlas.find(name + "-strobe");
    }

    public class ConfigurablePowerVoidBuild extends Building{
        public float powerConsumption = defaultPowerConsumption;

        @Override
        public void draw(){
            super.draw();

            ESRDrawf.setStrobeColor();
            Draw.rect(colorRegion, x, y);
            Draw.color();
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(Styles.black5, t -> {
                t.marginLeft(6f).marginRight(6f).right();
                t.field(String.valueOf(powerConsumption), text -> {
                    configure(Strings.parseFloat(text));
                }).width(120).valid(Strings::canParsePositiveFloat).get().setFilter(TextFieldFilter.floatsOnly);
                t.add(ESRUtls.statUnitName(StatUnit.powerSecond)).left();
            });
        }

        @Override
        public Object config(){
            return powerConsumption;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(powerConsumption);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            powerConsumption = read.f();
        }
    }
}
