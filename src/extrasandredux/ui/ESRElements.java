package extrasandredux.ui;

import arc.*;
import arc.flabel.*;

public class ESRElements{
    public static FLabel infinity(){
        return new FLabel("{wave}{rainbow}" + Core.bundle.get("esr-infinity"));
    }

    public static FLabel infiniteDamage(){
        return new FLabel("{wave}{rainbow}" + Core.bundle.get("esr-infinite-damage"));
    }

    public static FLabel everything(){
        return new FLabel("{wave}{rainbow}" + Core.bundle.get("esr-everything"));
    }
}
