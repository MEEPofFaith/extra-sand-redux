package extrasandredux.content;

import arc.audio.*;
import mindustry.*;

public class ESRSounds{
    public static Sound
        flowrateAbosrb = new Sound(),
        eviscerationCharge = new Sound(),
        eviscerationBlast = new Sound();

    public static void load(){
        flowrateAbosrb = Vars.tree.loadSound("flowrate-absorb");
        eviscerationCharge = Vars.tree.loadSound("evisceration-charge");
        eviscerationBlast = Vars.tree.loadSound("evisceration-blast");
    }
}
