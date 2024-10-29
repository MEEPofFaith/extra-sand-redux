package extrasandredux.world.blocks.defence.turret;

import arc.*;
import arc.graphics.g2d.*;
import extrasandredux.graphics.*;
import extrasandredux.util.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.*;

import static mindustry.Vars.*;

public class InfBuildTurret extends BuildTurret{
    public TextureRegion colorRegion;

    public InfBuildTurret(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.effect);
        range = 3000f * tilesize;
        targetInterval = 0; //H Y P E R S P E E D
    }


    @Override
    public void load(){
        super.load();
        colorRegion = Core.atlas.find(name + "-color");
    }

    public class InfBuildTurretBuild extends BuildTurretBuild{
        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.color();

            Draw.z(Layer.turret);

            Drawf.shadow(region, x - elevation, y - elevation, rotation - 90);
            Draw.rect(region, x, y, rotation - 90);

            ESRDrawf.setStrobeColor();
            Draw.rect(colorRegion, x, y, rotation - 90);
            Draw.color();

            if(glowRegion.found()){
                Drawf.additive(glowRegion, heatColor, warmup, x, y, rotation - 90f, Layer.turretHeat);
            }

            if(efficiency > 0){
                unit.drawBuilding();
            }
        }
    }
}
