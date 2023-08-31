package extrasandredux.world.blocks.storage;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import extrasandredux.graphics.*;
import extrasandredux.util.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;

import static mindustry.Vars.content;

public class ConfigurableContainer extends Block{
    protected static int storageCapacitySetting;

    public int initialStoraeCapacity = 1000;
    public TextureRegion colorRegion;

    public ConfigurableContainer(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.effect);

        acceptsItems = true;
        hasItems = true;
        solid = true;
        update = false;
        destructible = true;
        configurable = saveConfig = true;
        separateItemCapacity = true;
        group = BlockGroup.transportation;
        flags = EnumSet.of(BlockFlag.storage);
        allowResupply = true;

        config(Integer.class, (ConfigurableContainerBuild build, Integer cap) -> {
            build.storageCapacity = cap;
            build.items.each((i, a) -> build.items.set(i, Math.min(a, cap)));
        });
        config(Boolean.class, (ConfigurableContainerBuild build, Boolean incin) -> build.incinerate = incin);
        config(Object[].class, (ConfigurableContainerBuild build, Object[] config) -> {
            int cap = (int)config[0];
            build.storageCapacity = cap;
            build.items.each((i, a) -> build.items.set(i, Math.min(a, cap)));

            build.incinerate = (boolean)config[1];
        });
    }

    @Override
    public void load(){
        super.load();
        colorRegion = Core.atlas.find(name + "-strobe");
    }

    @Override
    public void setBars(){
        super.setBars();

        removeBar("items");
        addBar("items", (ConfigurableContainerBuild entity) -> new Bar(
            () -> Core.bundle.format("bar.items", entity.items.total()),
            () -> Pal.items,
            () -> entity.items.total() / ((float)entity.storageCapacity * content.items().count(UnlockableContent::unlockedNow))
        ));
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    public class ConfigurableContainerBuild extends Building{
        public int storageCapacity = initialStoraeCapacity;
        public boolean incinerate = false;

        @Override
        public void draw(){
            super.draw();

            ESRDrawf.setStrobeColor();
            Draw.rect(colorRegion, x, y);
            Draw.color();
        }

        @Override
        public void handleItem(Building source, Item item){
            if(incinerate && items.get(item) >= storageCapacity){
                StorageBlock.incinerateEffect(this, source);
                return;
            }
            super.handleItem(source, item);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return incinerate ? storageCapacity * 20 : storageCapacity;
        }

        @Override
        public void buildConfiguration(Table table){
            storageCapacitySetting = storageCapacity;
            table.table(Styles.black5, t -> {
                t.defaults().left();
                t.margin(6f);
                t.table(s -> {
                    s.marginLeft(6f).marginRight(6f).right();
                    s.field(String.valueOf(storageCapacitySetting), text -> {
                        storageCapacitySetting = Strings.parseInt(text);
                    }).width(120).valid(Strings::canParsePositiveInt).get().setFilter(TextFieldFilter.digitsOnly);
                    s.add(ESRUtls.statUnitName(StatUnit.items)).left();
                    s.button(Icon.save, () -> configure(storageCapacitySetting));
                });
                t.row();
                CheckBox box = new CheckBox("@esr-container.incinerate-overflow");
                box.changed(() -> configure(!incinerate));
                box.setChecked(incinerate);
                box.update(() -> box.setChecked(incinerate));
                t.add(box);
            });
        }

        @Override
        public void overwrote(Seq<Building> previous){
            for(Building other : previous){
                if(other.items != null && other.items != items){
                    items.add(other.items);
                }
            }

            items.each((i, a) -> items.set(i, Math.min(a, itemCapacity)));
        }

        @Override
        public Object config(){
            return new Object[]{storageCapacity, incinerate};
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(storageCapacity);
            write.bool(incinerate);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            storageCapacity = read.i();
            incinerate = read.bool();
        }
    }
}
