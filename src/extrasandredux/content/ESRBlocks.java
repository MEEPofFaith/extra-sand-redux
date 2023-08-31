package extrasandredux.content;

import arc.graphics.*;
import extrasandredux.world.blocks.defence.*;
import extrasandredux.world.blocks.defence.turret.*;
import extrasandredux.world.blocks.effect.*;
import extrasandredux.world.blocks.heat.*;
import extrasandredux.world.blocks.power.*;
import extrasandredux.world.blocks.sourcesvoids.*;
import extrasandredux.world.blocks.storage.*;
import extrasandredux.world.blocks.units.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.world.*;

public class ESRBlocks{
    public static Block
        //Turret
        eviscerator, everythingGun,

        //Distribution
        everythingItemSource, //TODO replace with a smart item source - link to blocks and fill with items based on consume filter

        //Liquid
        everythingLiquidSource, //TODO replace with a smart liquid source - link to blocks and fill with items based on consume filter
        configurableLiquidContainer,

        //Power
        configurablePowerSource, configurablePowerVoid, configurableBattery, smartPowerSource,

        //Defense
        sandboxWall, sandboxWallLarge, targetDummyBase,

        //Heat
        configurableHeatSource,

        //Unit
        allWeaponsUnitSpawner, capBlock,

        //More sources/voids
        multiSource, multiVoid, multiSourceVoid, multiEverythingSourceVoid,

        //Storage
        configurableContainer, placeableCore,

        //Effect
        configurableMendProjector, configurableOverdriveProjector;

    public static void load(){
        //Turret
        eviscerator = new EviscerationTurret("harbinger"){{ //TODO make it look like a duo and then have it expand to oh go oh god oh god
            float brange = 900f;

            size = 8;
            shake = 150f;
            range = brange;
            recoil = 8f;
            shootY = 16f;
            rotateSpeed = 0.3f;
            shootCone = 20f;
            cooldownTime = 600f;
            recoilTime = 600f;
            reload = 450f;
            moveWhileCharging = false;
            chargeSound = ESRSounds.eviscerationCharge;
            shootSound = ESRSounds.eviscerationBlast;
            shootType = new LaserBulletType(Float.MAX_VALUE){
                {
                    colors = new Color[]{Color.valueOf("F3E97966"), Color.valueOf("F3E979"), Color.white};
                    length = brange;
                    width = 75f;
                    lifetime = 130;
                    lightColor = colors[1];
                    ammoMultiplier = 1;

                    lightningSpacing = 20f;
                    lightningLength = 15;
                    lightningLengthRand = 10;
                    lightningDelay = 0.5f;
                    lightningDamage = Float.MAX_VALUE;
                    lightningAngleRand = 45f;
                    lightningColor = colors[1];

                    sideAngle = 25f;
                    sideWidth = width / 8f;
                    sideLength = length / 1.5f;

                    chargeEffect = ESRFx.eviscerationCharge;
                }

                @Override
                public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct){
                    super.hitTile(b, build, x, y, initialHealth, direct);
                    if(build.team != b.team) build.kill();
                }

                @Override
                public void hitEntity(Bullet b, Hitboxc other, float initialHealth){
                    super.hitEntity(b, other, initialHealth);
                    if(((Teamc)other).team() != b.team) ((Healthc)other).kill();
                }
            };

            shoot = new ShootSpread(){{
                shots = 100;
                spread = 55f / shots;
                firstShotDelay = ESRFx.eviscerationCharge.lifetime;
            }};
            inaccuracy = 15f;

            consumePower(300f);
        }};

        everythingGun = new EverythingTurret("everything-gun"){{
            size = 6;
            rotateSpeed = 20f;
            reload = 1f;
            range = 4400f;
            shootCone = 360f;
        }};

        //Distribution
        everythingItemSource = new EverythingItemSource("everything-item-source");

        //Liquid
        everythingLiquidSource = new EverythingLiquidSource("everything-liquid-source");

        //Power
        configurablePowerSource = new ConfigurablePowerSource("configurable-power-source");
        configurablePowerVoid = new ConfigurablePowerVoid("configurable-power-void");
        configurableBattery = new ConfigurableBattery("configurable-battery");
        smartPowerSource = new SmartPowerSource("smart-power-source");

        //Defense
        sandboxWall = new SandboxWall("sandbox-wall");

        sandboxWallLarge = new SandboxWall("sandbox-wall-large"){{
            size = 2;
        }};

        targetDummyBase = new TargetDummyBase("target-dummy-base"){{
            size = 2;
            pullScale = 0.1f;
        }};

        //Heat
        configurableHeatSource = new ConfigurableHeatSource("infini-heater");

        //Unit
        capBlock = new CapBlock("cap-block"){{
            unitCapModifier = 25;
        }};

        allWeaponsUnitSpawner = new BoxedUnit("boxed-flarogus", ESRUnitTypes.allWeaponsUnit){{
            size = 2;
        }};

        //More sources/voids
        multiSource = new MultiSource("multi-source");
        multiVoid = new MultiVoid("multi-void");
        multiSourceVoid = new MultiSourceVoid("multi-source-void");
        multiEverythingSourceVoid = new EverythingSourceVoid("material-source-void");

        //Storage
        configurableContainer = new ConfigurableContainer("configurable-container"){{
            size = 2;
        }};

        //Effect
        configurableMendProjector = new ConfigurableMendProjector("infini-mender");
        configurableOverdriveProjector = new ConfigurableOverdriveProjector("infini-overdrive");
    }
}
