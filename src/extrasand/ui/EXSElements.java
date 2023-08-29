package extrasand.ui;

import arc.*;
import arc.flabel.*;

public class EXSElements{
    public static FLabel infinity(){
        return new FLabel("{wave}{rainbow}" + Core.bundle.get("exs-infinity"));
    }

    public static FLabel infiniteDamage(){
        return new FLabel("{wave}{rainbow}" + Core.bundle.get("exs-infinite-damage"));
    }

    public static FLabel everything(){
        return new FLabel("{wave}{rainbow}" + Core.bundle.get("exs-everything"));
    }
}
