package extrasandredux.world.blocks.units;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import extrasandredux.graphics.*;
import extrasandredux.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.*;

public class BoxedUnit extends Block{
    public UnitType type;
    public TextureRegion colorRegion;

    public BoxedUnit(String name, UnitType type){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.units);
        this.type = type;

        destructible = true;
        solid = true;
        configurable = true;
        drawDisabled = false;
        canOverdrive = false;
        rebuildable = false;

        config(Boolean.class, (BoxedUnitBuild build, Boolean ignored) -> build.spawn());
    }

    @Override
    public void load(){
        super.load();
        colorRegion = Core.atlas.find(name + "-strobe");
    }

    public class BoxedUnitBuild extends Building{
        @Override
        public void draw(){
            super.draw();

            ESRDrawf.setStrobeColor();
            Draw.rect(colorRegion, x, y);
            Draw.color();
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.enabled && !Mathf.zero(p1)) spawn();
        }

        @Override
        public boolean canControlSelect(Unit player){
            return player.isPlayer();
        }

        @Override
        public void onControlSelect(Unit unit){
            if(!unit.isPlayer()) return;
            Player player = unit.getPlayer();

            Unit u = spawn();
            u.spawnedByCore(true);
            u.apply(StatusEffects.disarmed, 10f); //Short period of disarm so that the ctrl + click from selecting doesn't make you shoot
            Call.unitControl(player, u);
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.upload, () -> configure(true));
        }

        protected Unit spawn(){
            Unit u = type.spawn(self(), team);
            Fx.spawn.at(this);
            kill();
            u.rotation(90f);
            return u;
        }
    }
}
