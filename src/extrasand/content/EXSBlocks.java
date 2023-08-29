package extrasand.content;

import extrasand.world.blocks.defence.*;
import extrasand.world.blocks.effect.*;
import extrasand.world.blocks.sourcesvoids.*;
import mindustry.world.*;

public class EXSBlocks{
    public static Block

    //Turret
    eviscerator, everythingGun,

    //Distribution
    everythingItemSource,

    //Liquid
    everythingLiquidSource,

    //Power
    strobeNode, strobeInf, strobeBoost,

    //Defense
    sandboxWall, sandboxWallLarge, targetDummyBase,

    //Heat
    infiniHeatSource,

    //Unit
    godFactory, capBlock,

    //Items
    multiSource, multiVoid, multiSourceVoid, multiEverythingSourceVoid,

    //Effect
    infiniMender, infiniOverdrive;

    public static void load(){
        //Turret

        //Distribution
        everythingItemSource = new EverythingItemSource("everything-item-source");

        //Liquid
        everythingLiquidSource = new EverythingLiquidSource("everything-liquid-source");

        //Power

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

        //Unit

        //More sources/voids
        multiSource = new MultiSource("multi-source");
        multiVoid = new MultiVoid("multi-void");
        multiSourceVoid = new MultiSourceVoid("multi-source-void");
        multiEverythingSourceVoid = new EverythingSourceVoid("material-source-void");

        //Effect
        infiniMender = new SandboxMendProjector("infini-mender");
        infiniOverdrive = new SandboxOverdriveProjector("infini-overdrive");
    }
}
