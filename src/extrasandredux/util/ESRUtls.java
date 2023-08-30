package extrasandredux.util;

import arc.util.*;
import mindustry.core.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class ESRUtls{
    public static void applySandboxDefaults(Block block, Category category){
        block.requirements(category, BuildVisibility.sandboxOnly, ItemStack.empty);
        block.alwaysUnlocked = true;
        block.envEnabled = Env.any;
    }

    public static String statUnitName(StatUnit statUnit){
        return statUnit.icon != null ? statUnit.icon + " " + statUnit.localized() : statUnit.localized();
    }

    public static String round(float f){
        if(f >= 1_000_000_000){
            return Strings.autoFixed(f / 1_000_000_000, 1) + UI.billions;
        }else if(f >= 1_000_000){
            return Strings.autoFixed(f / 1_000_000, 1) + UI.millions;
        }else if(f >= 1000){
            return Strings.autoFixed(f / 1000, 1) + UI.thousands;
        }else{
            return Strings.autoFixed(f, 2);
        }
    }
}
