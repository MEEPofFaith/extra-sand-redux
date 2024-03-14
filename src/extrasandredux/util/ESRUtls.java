package extrasandredux.util;

import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.entities.bullet.*;
import mindustry.mod.Mods.*;
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

    public static float bulletDamage(BulletType b, float lifetime){
        if(b.spawnUnit != null){ //Missile unit damage
            if(b.spawnUnit.weapons.isEmpty()) return 0f;
            Weapon uW = b.spawnUnit.weapons.first();
            return bulletDamage(uW.bullet, uW.bullet.lifetime) * uW.shoot.shots;
        }else{
            float damage = b.damage + b.splashDamage; //Base Damage
            damage += b.lightningDamage * b.lightning * b.lightningLength; //Lightning Damage

            if(b.fragBullet != null){ //Frag Bullet Damage
                damage += bulletDamage(b.fragBullet, b.fragBullet.lifetime) * b.fragBullets;
            }

            if(b.intervalBullet != null){ //Interval Bullet Damage
                int amount = (int)(lifetime / b.bulletInterval * b.intervalBullets);
                damage += bulletDamage(b.intervalBullet, b.intervalBullet.lifetime) * amount;
            }

            if(b instanceof ContinuousBulletType cB){ //Continuous Damage
                return damage * lifetime / cB.damageInterval;
            }else{
                return damage;
            }
        }
    }

    /** Similar to {@link UI#formatAmount(long)} but for floats. */
    public static String round(float f){
        //prevent things like bars displaying erroneous representations of casted infinities
        if(f == Float.MAX_VALUE) return "∞";
        if(f == Float.MIN_VALUE) return "-∞";

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

    public static boolean modEnabled(String name){
        LoadedMod mod = Vars.mods.getMod(name);
        return mod != null && mod.isSupported() && mod.enabled();
    }
}
