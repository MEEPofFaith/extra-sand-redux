package extrasandredux.world.blocks.units;

import arc.graphics.g2d.*;
import extrasandredux.util.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.meta.*;

public class CapBlock extends Wall{
    public CapBlock(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.units);
    }

    public class CapBlockBuild extends WallBuild{
        @Override
        public void draw(){
            Draw.rect(region, x, y);
    
            drawTeamTop();
        }
    }
}
