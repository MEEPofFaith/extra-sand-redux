package extrasandredux;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import extrasandredux.content.*;
import extrasandredux.gen.entities.*;
import extrasandredux.graphics.*;
import extrasandredux.ui.*;
import extrasandredux.util.*;
import extrasandredux.world.blocks.defence.turret.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;

import static arc.Core.*;
import static extrasandredux.content.ESRBlocks.*;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;

public class ExtraSandRedux extends Mod{
    public static Seq<BulletData> allBullets = new Seq<>();
    public static int sandboxBlockHealthMultiplier = 1000000;
    public static boolean everything = false;

    public static FlowrateVoidDialog flowrateVoidDialog;

    public ExtraSandRedux(){
        Events.on(ClientLoadEvent.class, e -> {
            ESRPal.init();
            loadSettings();
        });

        // Load all assets once they're added into Vars.tree
        Events.on(FileTreeInitEvent.class, e -> {
            if(!headless){
                app.post(() -> {
                    ESRSounds.load();
                    ESRShaders.init();
                });
            }
        });

        // Check if everything turret/unit are enabled
        everything = settings.getBool("esr-sandbox-everything", false);

        //Make sandbox blocks have a ton of health
        if(settings.getBool("esr-sandbox-health", false)){
            Events.on(ContentInitEvent.class, e -> {
                Seq<Block> sandboxBlocks = Seq.with(
                    //Vanilla
                    itemSource, itemVoid,
                    liquidSource, liquidVoid,
                    powerSource, powerVoid,
                    payloadSource, payloadVoid,
                    heatSource,

                    //ESR
                    eviscerator, everythingGun,
                    everythingItemSource,
                    everythingLiquidSource,
                    configurablePowerSource, configurablePowerVoid, configurableBattery, smartPowerSource,
                    configurableHeatSource,
                    boxedFlarogus, capBlock,
                    capacityConfigurer,
                    configurableContainer, placeableCore, inputReader,
                    multiSource, multiVoid, multiSourceVoid, multiEverythingSourceVoid,
                    infBuildTurret,
                    configurableMendProjector, configurableOverdriveProjector,
                    turretController
                );
                //Can't use b.buildVisibility == BuildVisibility.sandboxOnly because some things, like scrap walls, are also sandbox only.

                sandboxBlocks.each(b -> b.health *= sandboxBlockHealthMultiplier);
            });
        }
    }

    @Override
    public void init(){
        Events.on(ClientLoadEvent.class, e -> {
            if(!headless){
                LoadedMod esr = mods.locateMod("extra-sand-redux");
                Func<String, String> getModBundle = value -> bundle.get("mod." + esr.meta.name + "." + value);

                esr.meta.description = getModBundle.get("description");
                esr.meta.subtitle = getModBundle.get("subtitle");

                flowrateVoidDialog = new FlowrateVoidDialog();
            }

            if(everything){
                godHood(ESRUnitTypes.allWeaponsUnit);
                setupEveryBullets((EverythingTurret)ESRBlocks.everythingGun);
            }
        });
    }

    @Override
    public void loadContent(){
        EntityRegistry.register();

        ESRUnitTypes.load();
        ESRBlocks.load();
    }

    private void loadSettings(){
        ui.settings.addCategory(bundle.get("setting.esr-title"), "extra-sand-redux-settings-icon", t -> {
            t.sliderPref("esr-strobespeed", 3, 1, 20, 1, s -> Strings.autoFixed(s / 2f, 2));
            t.checkPref("esr-sandbox-health", false);
            t.checkPref("esr-sandbox-everything", false);
        });
    }

    public static void godHood(UnitType ascending){
        int[] index = {0};
        ascending.health = 0;
        ascending.itemCapacity = 0;
        content.units().each(u -> u != ascending, u -> {
            u.weapons.each(w -> {
                if(!checkKillShooter(w.bullet)){
                    Weapon copy = w.copy();
                    ascending.weapons.add(copy);
                    if(w.otherSide != -1){
                        int diff = w.otherSide - u.weapons.get(w.otherSide).otherSide;
                        copy.otherSide = index[0] + diff;
                    }

                    if(copy.rotate) copy.rotateSpeed = 360f;
                    copy.shootCone = 360f;

                    if(Math.abs(copy.bullet.recoil) > 0){
                        copy.bullet = copy.bullet.copy();
                        copy.bullet.recoil = 0f;
                    }

                    if(copy.shootStatus == StatusEffects.unmoving || copy.shootStatus == StatusEffects.slow){
                        copy.shootStatus = StatusEffects.none;
                    }
                    index[0]++;
                }
            });

            u.abilities.each(a -> !(a instanceof MoveLightningAbility m) || !checkKillShooter(m.bullet), a -> ascending.abilities.add(a));

            ascending.health += u.health;
            ascending.itemCapacity += u.itemCapacity;
            ascending.range = Math.max(ascending.range, u.range);
            ascending.hitSize = Math.max(ascending.hitSize, u.hitSize);
        });
    }

    public static boolean checkKillShooter(BulletType b){
        if(b == null || b == Bullets.damageLightning || b == Bullets.damageLightningGround || b == Bullets.damageLightningAir) return false;
        return b.killShooter ||
            checkKillShooter(b.fragBullet) ||
            checkKillShooter(b.intervalBullet) ||
            checkKillShooter(b.lightningType) ||
            b.spawnBullets.contains(ExtraSandRedux::checkKillShooter);
    }

    public static void setupEveryBullets(Turret base){
        content.units().each(u -> u.weapons.each(w -> w.bullet != null, w -> {
            BulletType bul = w.bullet;
            BulletData data = new BulletData(bul, w.shootSound, bul.shootEffect, bul.smokeEffect, w.shake, bul.lifetime);
            if(!allBullets.contains(data)){
                allBullets.add(data);
            }
        }));
        content.blocks().each(b -> b instanceof Turret, b -> {
            if(b != base){
                if(b instanceof LaserTurret block && block.shootType != null){
                    BulletType bul = block.shootType;
                    Effect fshootEffect = block.shootEffect == null ? bul.shootEffect : block.shootEffect;
                    Effect fsmokeEffect = block.smokeEffect == null ? bul.smokeEffect : block.smokeEffect;
                    BulletData data = new BulletData(bul, block.shootSound, fshootEffect, fsmokeEffect, block.shake, bul.lifetime + block.shootDuration, true);
                    allBullets.add(data);
                }else if(b instanceof PowerTurret block && block.shootType != null){
                    BulletType bul = block.shootType;
                    Effect fshootEffect = block.shootEffect == null ? bul.shootEffect : block.shootEffect;
                    Effect fsmokeEffect = block.smokeEffect == null ? bul.smokeEffect : block.smokeEffect;
                    BulletData data = new BulletData(bul, block.shootSound, fshootEffect, fsmokeEffect, block.shake, bul.lifetime);
                    allBullets.add(data);
                }else if(b instanceof ItemTurret block){
                    for(BulletType bul : block.ammoTypes.values()){
                        Effect fshootEffect = block.shootEffect == null ? bul.shootEffect : block.shootEffect;
                        Effect fsmokeEffect = block.smokeEffect == null ? bul.smokeEffect : block.smokeEffect;
                        BulletData data = new BulletData(bul, block.shootSound, fshootEffect, fsmokeEffect, block.shake, bul.lifetime);
                        allBullets.add(data);
                    }
                }else if(b instanceof LiquidTurret block){
                    for(BulletType bul : block.ammoTypes.values()){
                        Effect fshootEffect = block.shootEffect == null ? bul.shootEffect : block.shootEffect;
                        Effect fsmokeEffect = block.smokeEffect == null ? bul.smokeEffect : block.smokeEffect;
                        BulletData data = new BulletData(bul, block.shootSound, fshootEffect, fsmokeEffect, block.shake, bul.lifetime);
                        allBullets.add(data);
                    }
                }else if(b instanceof PayloadAmmoTurret block){
                    for(BulletType bul : block.ammoTypes.values()){
                        Effect fshootEffect = block.shootEffect == null ? bul.shootEffect : block.shootEffect;
                        Effect fsmokeEffect = block.smokeEffect == null ? bul.smokeEffect : block.smokeEffect;
                        BulletData data = new BulletData(bul, block.shootSound, fshootEffect, fsmokeEffect, block.shake, bul.lifetime);
                        allBullets.add(data);
                    }
                }
            }
        });

        allBullets.sort(b -> ESRUtls.bulletDamage(b.bulletType, b.lifetime));
    }

    public static class BulletData{
        public BulletType bulletType;
        public Sound shootSound;
        public Effect shootEffect, smokeEffect;
        public float shake, lifetime;
        public boolean continuousBlock;

        public BulletData(BulletType bulletType, Sound shootSound, Effect shakeEffect, Effect smokeEffect, float shake, float lifetime, boolean continuous){
            this.bulletType = bulletType;
            this.shootSound = shootSound;
            this.shootEffect = shakeEffect;
            this.smokeEffect = smokeEffect;
            this.shake = shake;
            this.lifetime = lifetime;
            this.continuousBlock = continuous;
        }

        public BulletData(BulletType bulletType, Sound shootSound, Effect shootEffect, Effect smokeEffect, float shake, float lifetime){
            this(bulletType, shootSound, shootEffect, smokeEffect, shake, lifetime, false);
        }

        @Override
        public boolean equals(Object obj){
            return obj instanceof BulletData o &&
                bulletType == o.bulletType &&
                shootSound == o.shootSound &&
                shootEffect == o.shootEffect &&
                smokeEffect == o.smokeEffect &&
                shake == o.shake &&
                lifetime == o.lifetime &&
                continuousBlock == o.continuousBlock;
        }
    }
}
