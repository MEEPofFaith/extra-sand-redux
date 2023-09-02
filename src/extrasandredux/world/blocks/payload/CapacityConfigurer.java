package extrasandredux.world.blocks.payload;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import extrasandredux.content.*;
import extrasandredux.graphics.*;
import extrasandredux.util.*;
import extrasandredux.world.blocks.power.ConfigurableBattery.*;
import extrasandredux.world.blocks.storage.ConfigurableContainer.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

public class CapacityConfigurer extends PayloadBlock{
    public TextureRegion colorRegion, topColorRegion;

    public CapacityConfigurer(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.units);

        size = 3;
        outputsPayload = true;
        rotate = true;
        canOverdrive = false;
        configurable = saveConfig = true;

        config(Integer.class, (CapacityConfigurerBuild build, Integer cap) -> build.configItemCap = cap);
        config(Float.class, (CapacityConfigurerBuild build, Float cap) -> build.configBatteryCap = cap);
        config(Object[].class, (CapacityConfigurerBuild build, Object[] config) -> {
            build.configItemCap = (int)config[0];
            build.configBatteryCap = (float)config[1];
        });
    }

    @Override
    public void load(){
        super.load();

        colorRegion = Core.atlas.find(name + "-strobe", "extra-sand-redux-factory-strobe-" + size + regionSuffix);
        topColorRegion = Core.atlas.find(name + "-top-strobe", "extra-sand-redux-factory-top-strobe-" + size + regionSuffix);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, inRegion, outRegion, topRegion};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(inRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
    }

    public class CapacityConfigurerBuild extends PayloadBlockBuild<Payload>{
        public boolean exporting;
        public int configItemCap = 1000;
        //public float configLiquidCap = 1000f;
        public float configBatteryCap = 1000f;

        @Override
        public void updateTile(){
            super.updateTile();

            if(exporting){
                moveOutPayload();
            }else if(moveInPayload()){
                configPayload();
            }
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            ESRDrawf.setStrobeColor();
            Draw.rect(colorRegion, x, y);
            Draw.color();

            //draw input
            boolean fallback = true;
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                    fallback = false;
                }
            }
            if(fallback) Draw.rect(inRegion, x, y, rotation * 90);

            Draw.rect(outRegion, x, y, rotdeg());

            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
            ESRDrawf.setStrobeColor();
            Draw.rect(topColorRegion, x, y);
            Draw.color();
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            super.handlePayload(source, payload);
            exporting = false;
        }

        public void configPayload(){
            if(payload instanceof BuildPayload p){
                Color configColor = null;
                if(p.build instanceof ConfigurableContainerBuild c && c.storageCapacity != configItemCap){
                    c.configure(configItemCap);
                    configColor = Pal.items;
                }
                if(p.build instanceof ConfigurableBatteryBuild b && b.powerCapacity != configBatteryCap){
                    b.configure(configBatteryCap);
                    configColor = Pal.powerBar;
                }

                if(configColor != null){
                    ESRFx.storageConfiged.at(p.build.x, p.build.y, p.block().size, configColor, p.build);
                }
            }

            exporting = true;
        }

        @Override
        public void buildConfiguration(Table table){
            super.buildConfiguration(table);
            table.table(Styles.black6, t -> {
                t.defaults().left();
                t.margin(6f);
                t.add("@block.extra-sand-redux-configurable-container.name").colspan(2);
                t.row();
                t.field(String.valueOf(configItemCap), text -> {
                    configure(Strings.parseInt(text));
                }).width(120).valid(Strings::canParsePositiveInt).padLeft(8f).get().setFilter(TextFieldFilter.digitsOnly);
                t.add(ESRUtls.statUnitName(StatUnit.items)).left();
                t.row();
                t.add("@block.extra-sand-redux-configurable-battery.name").colspan(2);
                t.row();
                t.field(String.valueOf(configBatteryCap), text -> {
                    configure(Strings.parseFloat(text));
                }).width(120).valid(text -> Strings.canParseFloat(text) && Strings.parseFloat(text) > 0).padLeft(8f).get().setFilter(TextFieldFilter.floatsOnly);
                t.add(ESRUtls.statUnitName(StatUnit.powerUnits)).left();
            });
        }

        @Override
        public Object config(){
            return new Object[]{configItemCap, configBatteryCap};
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(exporting);
            write.i(configItemCap);
            write.f(configBatteryCap);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            exporting = read.bool();
            configItemCap = read.i();
            configBatteryCap = read.f();
        }
    }
}
