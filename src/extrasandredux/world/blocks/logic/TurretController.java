package extrasandredux.world.blocks.logic;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import extrasandredux.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.Turret.*;

import static mindustry.Vars.*;

public class TurretController extends Block{
    protected static Vec2 tmpVec = new Vec2();

    protected TextureRegion modeRegion;
    protected TextureRegion plugRegion0, plugRegion1;

    public TurretController(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.logic);

        update = true;
        solid = true;
        rotate = true;
        rotateDraw = false;
        configurable = true;
        saveConfig = false;
        commandable = true;

        config(Integer.class, (TurretControllerBuild tile, Integer state) -> {
            tile.controlState = ControlState.values()[state];
        });
        config(Vec2.class, (TurretControllerBuild tile, Vec2 targetSetting) -> {
            tile.targetSetting.set(targetSetting);
        });
    }

    @Override
    public void load(){
        super.load();

        modeRegion = Core.atlas.find(name + "-mode");
        plugRegion0 = Core.atlas.find(name + "-plug0");
        plugRegion1 = Core.atlas.find(name + "-plug1");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(name + "-preview")};
    }

    @Override
    public TextureRegion getPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        return region;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        super.drawPlanRegion(plan, list);
        drawPlug(plan.x * 8f, plan.y * 8f, plan.rotation);
    }

    public void drawPlug(float x, float y, int rotation){
        TextureRegion region = rotation > 1 ? plugRegion1 : plugRegion0;
        float flip = rotation % 2 == 0 ? 1 : -1;
        Draw.rect(region, x, y, region.width / 4f, flip * region.height / 4f, rotation * 90f);
    }

    public class TurretControllerBuild extends Building{
        public ControlState controlState = ControlState.off;
        /** x = angle (degrees), y = distance (tiles) */
        public Vec2 targetSetting = new Vec2();
        public TextField angField, dstField;

        @Override
        public Vec2 getCommandPosition(){
            if(front() instanceof TurretBuild b && b.team == team){
                tmpVec.trns(targetSetting.x, targetSetting.y * tilesize).add(b);
                return tmpVec;
            }else{
                return null;
            }
        }

        @Override
        public void onCommand(Vec2 target){
            if(front() instanceof TurretBuild b && b.team == team){
                targetSetting.set(b.angleTo(target), b.dst(target) / tilesize);
                if(angField != null){
                    angField.setText("" + targetSetting.x);
                    dstField.setText("" + targetSetting.y);
                }
            }
        }

        @Override
        public void created(){
            targetSetting.set(90, 10);
        }

        @Override
        public void updateTile(){
            Building front = front();
            if(front instanceof TurretBuild b && b.team == team){
                if(controlState == ControlState.on){
                    b.control(LAccess.enabled, 1, 0, 0, 0);
                    Tmp.v1.trns(targetSetting.x, targetSetting.y * tilesize).add(b).scl(1f / tilesize);
                    b.control(LAccess.shoot, Tmp.v1.x, Tmp.v1.y, 1, 0);
                }else if(controlState == ControlState.disable && !b.isControlled()){
                    b.control(LAccess.enabled, 0, 0, 0, 0);
                }else{
                    b.control(LAccess.enabled, 1, 0, 0, 0);
                    if(b.logicShooting){ //If was shooting, stop
                        b.logicControlTime = 0f;
                        b.logicShooting = false;
                    }
                }
            }
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(Styles.black5, t -> {
                //Mode
                t.table(mode -> {
                    ButtonGroup<TextButton> group = new ButtonGroup<>();

                    mode.button("@esr-turret-controller-on", Styles.flatTogglet, () -> {
                        controlState = ControlState.on;
                        configure(1);
                    }).group(group).wrapLabel(false).grow()
                        .update(tb -> tb.setChecked(controlState == ControlState.on))
                        .get().margin(0f, 6f, 0f, 6f);
                    mode.row();
                    mode.button("@esr-turret-controller-off", Styles.flatTogglet, () -> {
                        controlState = ControlState.off;
                        configure(0);
                    }).group(group).wrapLabel(false).grow()
                        .update(tb -> tb.setChecked(controlState == ControlState.off))
                        .get().margin(0f, 6f, 0f, 6f);
                    mode.row();
                    mode.button("@esr-turret-controller-disable", Styles.flatTogglet, () -> {
                        controlState = ControlState.disable;
                        configure(2);
                    }).group(group).wrapLabel(false).grow()
                        .update(tb -> tb.setChecked(controlState == ControlState.disable))
                        .get().margin(0f, 6f, 0f, 6f);
                }).top().growY();

                //Target
                t.table(tar -> { //TODO target using a click. Command mode probably.
                    tar.add("@esr-turret-controller-angle").right();
                    angField = tar.field("" + targetSetting.x, TextFieldFilter.floatsOnly, s -> {
                        targetSetting.x = Strings.parseFloat(s);
                        configure(targetSetting);
                    }).get();
                    tar.row();
                    tar.add("@esr-turret-controller-distance").right();
                    dstField = tar.field("" + targetSetting.y, TextFieldFilter.floatsOnly, s -> {
                        targetSetting.y = Strings.parseFloat(s);
                        configure(targetSetting);
                    }).get();
                }).top().growY().padLeft(6f);
            });
        }

        @Override
        public void draw(){
            super.draw();

            Draw.color(controlState.modeColor);
            Draw.rect(modeRegion, x, y);
            Draw.color();
            Draw.z(Layer.block + 0.01f);
            drawPlug(x, y, rotation);
        }

        @Override
        public void drawSelect(){
            Building front = front();
            if(front instanceof TurretBuild b && b.team == team){
                Lines.stroke(1, team.color);
                Tmp.v1.trns(targetSetting.x, targetSetting.y * tilesize).add(b);
                Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
                Drawf.target(Tmp.v1.x, Tmp.v1.y, 4, team.color);
            }
        }

        @Override
        public void write(Writes write){
            write.i(controlState.ordinal());
            write.f(targetSetting.x);
            write.f(targetSetting.y);
        }

        @Override
        public void read(Reads read, byte revision){
            controlState = ControlState.all[read.i()];
            float tX = read.f();
            float tY = read.f();
            targetSetting.set(tX, tY);
        }
    }

    public enum ControlState{
        off(Pal.darkerGray),
        on(Pal.heal),
        disable(Color.valueOf("ff4545"));

        public Color modeColor;

        public static ControlState[] all = values();

        ControlState(Color modeColor){
            this.modeColor = modeColor;
        }

        public ControlState next(){
            int next = ordinal() + 1;
            if(next >= all.length) next = 0;
            return all[next];
        }
    }
}
