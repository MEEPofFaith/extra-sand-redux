package extrasandredux.content;

import arc.audio.*;
import mindustry.*;

public class ESRSounds{
    public static Sound
        eviscerationCharge = new Sound(),
        eviscerationBlast = new Sound();

    public static void load(){
        if(Vars.headless) return;

        eviscerationCharge = Vars.tree.loadSound("evisceration-charge");
        eviscerationBlast = Vars.tree.loadSound("evisceration-blast");
    }
}
