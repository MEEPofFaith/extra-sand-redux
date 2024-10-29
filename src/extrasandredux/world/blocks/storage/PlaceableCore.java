package extrasandredux.world.blocks.storage;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import extrasandredux.util.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class PlaceableCore extends CoreBlock{
    public PlaceableCore(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.effect);

        configurable = true;
        saveConfig = false;

        config(Boolean.class, (PlaceableCoreBuild build, Boolean ignored) -> {
            build.tile.setBlock(build.toPlace, build.team);
            Events.fire(new BlockBuildBeginEvent(build.tile, build.team, null, false));
            build.toPlace.placeBegan(build.tile, this, null);
        });
        config(Block.class, (PlaceableCoreBuild build, Block block) -> build.toPlace = block);
        configClear((PlaceableCoreBuild build) -> build.toPlace = null);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.unitType);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return true;
    }

    @Override
    public boolean canBreak(Tile tile){
        return true;
    }

    public class PlaceableCoreBuild extends CoreBuild{
        public Block toPlace;

        @Override
        public void draw(){
            super.draw();

            if(toPlace != null){
                Draw.alpha(0.25f + Mathf.absin(50f / Mathf.PI2, 0.25f));
                Draw.rect(toPlace.fullIcon, x + toPlace.offset, y + toPlace.offset);
            }
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(t -> {
                ImageButton ib = t.button(Icon.add, Styles.flati, () -> configure(true)).fillX().height(32f).disabled(b -> toPlace == null).get();
                ib.getStyle().disabled = ib.getStyle().over;
                ib.add("@esr-placeable-core-place").padLeft(16f);
                t.row();
                ItemSelection.buildTable(PlaceableCore.this, t, content.blocks().select(b -> b instanceof CoreBlock && !(b instanceof PlaceableCore)), () -> toPlace, this::configure, false, selectionRows, selectionColumns);
            });
        }
    }
}
