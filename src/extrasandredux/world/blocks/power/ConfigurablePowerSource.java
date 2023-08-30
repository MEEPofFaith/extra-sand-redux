package extrasandredux.world.blocks.power;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import extrasandredux.graphics.*;
import extrasandredux.util.*;
import extrasandredux.world.blocks.power.ConfigurablePowerVoid.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

public class ConfigurablePowerSource extends PowerBlock{
    public float defaultPowerProduction = 1000f;
    public TextureRegion colorRegion;

    public ConfigurablePowerSource(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.power);

        outputsPower = true;
        consumesPower = false;
        configurable = saveConfig = true;
        canOverdrive = false;

        config(Float.class, (ConfigurablePowerSourceBuild build, Float f) -> build.powerProduction = f);
    }

    @Override
    public void load(){
        super.load();
        colorRegion = Core.atlas.find(name + "-strobe");
    }

    public class ConfigurablePowerSourceBuild extends Building{
        public float powerProduction = defaultPowerProduction;

        @Override
        public void draw(){
            super.draw();

            ESRDrawf.setStrobeColor();
            Draw.rect(colorRegion, x, y);
            Draw.color();
        }

        @Override
        public float getPowerProduction(){
            return powerProduction / 60f;
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(Styles.black5, t -> {
                t.marginLeft(6f).marginRight(6f).right();
                t.field(String.valueOf(powerProduction), text -> {
                    configure(Strings.parseFloat(text));
                }).width(120).valid(Strings::canParsePositiveFloat).get().setFilter(TextFieldFilter.floatsOnly);
                t.add(ESRUtls.statUnitName(StatUnit.powerSecond)).left();
            });
        }

        @Override
        public Object config(){
            return powerProduction;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(powerProduction);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            powerProduction = read.f();
        }
    }
}
