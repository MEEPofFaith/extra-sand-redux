package extrasandredux.world.blocks.power;

import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;
import extrasandredux.graphics.*;
import extrasandredux.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.power.*;

public class SmartPowerSource extends PowerBlock{
    public float powerOverhead = 1000f / 60f;
    public TextureRegion colorRegion;

    public SmartPowerSource(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.power);

        outputsPower = true;
        consumesPower = false;
        canOverdrive = false;
    }

    @Override
    public void load(){
        super.load();
        colorRegion = Core.atlas.find(name + "-strobe");
    }

    public class SmartPowerSourceBuild extends Building{
        @Override
        public void draw(){
            super.draw();

            ESRDrawf.setStrobeColor();
            Draw.rect(colorRegion, x, y);
            Draw.color();
        }

        @Override
        public float getPowerProduction(){
            return enabled ? power.graph.getPowerNeeded() / Time.delta + powerOverhead : 0;
        }
    }
}
