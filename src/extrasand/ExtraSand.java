package extrasand;

import arc.*;
import arc.struct.*;
import arc.util.*;
import extrasand.content.*;
import extrasand.gen.entities.*;
import extrasand.graphics.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.world.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;

public class ExtraSand extends Mod{
    public static int sandboxBlockHealthMultiplier = 1000000;

    public ExtraSand(){
        Events.on(ClientLoadEvent.class, e -> {
            PMPal.init();
            loadSettings();
        });

        //Make sandbox blocks have a ton of health
        if(settings.getBool("exs-sandbox-health", false)){
            Events.on(ContentInitEvent.class, e -> {
                Seq<Block> sandboxBlocks = Seq.with(
                    //Vanilla
                    itemSource, itemVoid,
                    liquidSource, liquidVoid,
                    powerSource, powerVoid,
                    payloadSource, payloadVoid,
                    heatSource/*,

                    //ExS
                    eviscerator, everythingGun,
                    everythingItemSource, sandDriver,
                    everythingLiquidSource,
                    strobeNode, strobeInf, strobeBoost,
                    infiniHeatSource,
                    godFactory, capBlock,
                    multiSource, multiVoid, multiSourceVoid, multiEverythingSourceVoid,
                    infiniMender, infiniOverdrive*/
                );
                //Can't use b.buildVisibility == BuildVisibility.sandboxOnly because some things, like scrap walls, are also sandbox only.

                sandboxBlocks.each(b -> b.health *= sandboxBlockHealthMultiplier);
            });
        }
    }

    @Override
    public void loadContent(){
        EntityRegistry.register();

        EXSUnitTypes.load();
        EXSBlocks.load();
    }

    private void loadSettings(){
        ui.settings.addCategory(bundle.get("setting.exs-title"), "extra-sand-redux-settings-icon", t -> {
            t.sliderPref("exs-strobespeed", 3, 1, 20, 1, s -> Strings.autoFixed(s / 2f, 2));
            t.checkPref("exs-sandbox-health", false);
            t.checkPref("exs-sandbox-everything", false);
        });
    }
}
