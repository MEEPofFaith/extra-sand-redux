package extrasandredux.world.blocks.sourcesvoids;

import arc.*;
import arc.graphics.g2d.*;
import extrasandredux.graphics.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class MultiSourceVoid extends MultiSource{
    public TextureRegion rainbow;

    public MultiSourceVoid(String name){
        super(name);

        acceptsItems = hasLiquids = true;
    }

    @Override
    public void load(){
        super.load();

        rainbow = Core.atlas.find(name + "-rainbow");
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        return other.replaceable && (other != this || rotate) && this.group != BlockGroup.none && (other.group == BlockGroup.transportation || other.group == BlockGroup.liquids) &&
            (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
    }

    public class MultiSourceVoidBuild extends MultiSourceBuild{
        @Override
        public void draw(){
            super.draw();
            ESRDrawf.setStrobeColor();
            Draw.rect(rainbow, x, y);
            Draw.color();
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return enabled;
        }

        @Override
        public void handleItem(Building source, Item item){}

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return enabled;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){}
    }
}
