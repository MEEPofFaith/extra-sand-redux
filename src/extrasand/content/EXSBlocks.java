package extrasand.content;

import extrasand.world.blocks.defence.*;
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

        //Liquid

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

        //Effect
    }
}
