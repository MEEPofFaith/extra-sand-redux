package extrasandredux.world.blocks.sourcesvoids;

import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;
import extrasandredux.graphics.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class EverythingLiquidSource extends Block{
    public float ticksPerItemColor = 90f;

    public TextureRegion strobeRegion, centerRegion;

    public EverythingLiquidSource(String name){
        super(name);
        requirements(Category.liquid, BuildVisibility.sandboxOnly, ItemStack.empty);
        alwaysUnlocked = true;

        update = true;
        solid = true;
        hasLiquids = true;
        liquidCapacity = 100f;
        outputsLiquid = true;
        noUpdateDisabled = true;
        displayFlow = false;
        group = BlockGroup.liquids;
        envEnabled = Env.any;
    }

    @Override
    public void load(){
        super.load();
        strobeRegion = Core.atlas.find(name + "-strobe", "extra-sand-redux-source-strobe");
        centerRegion = Core.atlas.find(name + "-center", "center");
    }

    @Override
    public void setBars(){
        super.setBars();

        removeBar("liquid");
    }

    public class EverythingLiquidSourceBuild extends Building{
        @Override
        public void draw(){
            super.draw();

            ESRDrawf.setStrobeColor();
            Draw.rect(strobeRegion, x, y);

            Draw.color(Tmp.c1.lerp(ESRPal.liquidColors, Time.time / (ticksPerItemColor * ESRPal.liquidColors.length) % 1f));
            Draw.rect(centerRegion, x, y);
            Draw.color();
        }

        @Override
        public void updateTile(){
            Vars.content.liquids().each(l -> {
                liquids.add(l, liquidCapacity);
                dumpLiquid(l);
                liquids.clear();
            });
        }
    }
}
