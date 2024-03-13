package extrasandredux.ui;

import arc.*;
import arc.flabel.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;

public class ESRElements{
    public static void divider(Table t, String label, Color color, int colSpan){
        if(label != null){
            t.add(label).growX().color(color).colspan(colSpan).left();
            t.row();
        }
        t.image().growX().pad(5f, 0f, 5f, 0f)
            .height(3f).color(color).colspan(colSpan).left();
        t.row();
    }

    public static void divider(Table t, String label, Color color){
        divider(t, label, color, 1);
    }

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
