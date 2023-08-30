package extrasandredux.world.blocks.power;

import arc.util.*;
import extrasandredux.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.power.*;

public class SmartPowerSource extends PowerBlock{
    public float powerOverhead = 1000f / 60f;

    public SmartPowerSource(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.power);

        outputsPower = true;
        consumesPower = false;
        canOverdrive = false;
    }

    public class SmartPowerSourceBuild extends Building{
        @Override
        public float getPowerProduction(){
            return enabled ? power.graph.getPowerNeeded() / Time.delta + powerOverhead : 0;
        }
    }
}
