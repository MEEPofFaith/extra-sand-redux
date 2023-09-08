package extrasandredux.world.blocks.heat;

import arc.graphics.g2d.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import extrasandredux.util.*;
import extrasandredux.world.draw.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class ConfigurableHeatSource extends Block{
    public DrawBlock drawer = new DrawMulti(new DrawDefault(), new DrawStrobe(), new DrawHeatOutput());

    public ConfigurableHeatSource(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.crafting);

        configurable = saveConfig = true;

        update = true;
        solid = true;
        sync = true;
        flags = EnumSet.of(BlockFlag.factory);

        rotateDraw = false;
        rotate = true;
        canOverdrive = false;
        drawArrow = true;

        config(Float.class, (InfinitHeatSourceBuild build, Float f) -> build.heat = f);
    }

    @Override
    public void load(){
        super.load();

        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out){
        drawer.getRegionsToOutline(this, out);
    }

    public class InfinitHeatSourceBuild extends Building implements HeatBlock{
        public float heat = 20;

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(Styles.black5, t -> {
                t.margin(6f);
                t.field(String.valueOf(heat), text -> {
                    configure(Strings.parseFloat(text));
                }).width(120).valid(Strings::canParsePositiveFloat).get().setFilter(TextFieldFilter.floatsOnly);
                t.add(ESRUtls.statUnitName(StatUnit.heatUnits)).left();
            });
        }

        @Override
        public Object config(){
            return heat;
        }

        @Override
        public float warmup(){
            return 1f;
        }

        @Override
        public float heatFrac(){
            return 1f;
        }

        @Override
        public float heat(){
            return heat;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            heat = read.f();
        }
    }
}
