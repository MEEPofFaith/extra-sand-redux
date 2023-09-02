package extrasandredux.world.blocks.storage;

import arc.*;
import arc.audio.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import extrasandredux.util.*;
import extrasandredux.world.meta.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

public class InputReader extends PayloadVoid{
    protected static float addTimeSetting;

    public InputReader(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.effect);

        acceptsItems = hasItems = hasLiquids = hasPower = true;
        outputsPower = outputsPayload = false;
        configurable = true;
        saveConfig = false;
        payloadSpeed = 3f;
        itemCapacity = 10000;
        liquidCapacity = 10000f;

        config(Float.class, (InputReaderBuild build, Float time) -> {
            build.readingTimer += time * 60f;
            build.maxTime = build.readingTimer;
        });
        config(Boolean.class, (InputReaderBuild build, Boolean ignored) -> {
            build.readingTimer = 0f;
            build.totalTime = 0f;
            build.items.clear();
            build.liquids.clear();
            build.totalPowerProduced = build.totalPowerConsumed = 0f;
            build.payloads.clear();
        });
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public boolean isAccessible(){ //Dont show inventory
        return false;
    }

    @Override
    public void setBars(){
        super.setBars();

        removeBar("items");
        removeBar("liquid");

        addBar("time", (InputReaderBuild entity) -> new Bar(
            () -> UI.formatTime(entity.readingTimer) + " | " + UI.formatTime(entity.totalTime),
            () -> Pal.bar,
            () -> entity.readingTimer / entity.maxTime
        ));
        addBar("totalItems", (InputReaderBuild entity) -> new Bar(
            () -> getBarDisplay(entity.items.total(), entity.getTotalSeconds(), StatUnit.itemsSecond),
            () -> Pal.items,
            entity::barFill
        ));
        addBar("totalLiquids", (InputReaderBuild entity) -> new Bar(
            () -> getBarDisplay(entity.getTotalLiquids(), entity.getTotalSeconds(), StatUnit.liquidSecond),
            () -> Pal.items,
            entity::barFill
        ));
        addBar("totalPayloads", (InputReaderBuild entity) -> new Bar(
            () -> getBarDisplay(entity.payloads.total(), entity.getTotalSeconds(), ESRStatUnit.payloadSecond),
            () -> Pal.items,
            entity::barFill
        ));
        addBar("totalPowerProduced", (InputReaderBuild entity) -> new Bar(
            () -> getBarDisplay(entity.totalPowerProduced, entity.getTotalSeconds(), StatUnit.powerSecond),
            () -> Pal.powerBar,
            entity::barFill
        ));
        addBar("totalPowerConsumed", (InputReaderBuild entity) -> new Bar(
            () -> getBarDisplay(entity.totalPowerConsumed, entity.getTotalSeconds(), StatUnit.powerSecond),
            () -> Pal.powerBar,
            entity::barFill
        ));
    }

    public String getBarDisplay(float totalAmount, float totalTime, StatUnit unit){
        if(totalTime <= 0) return Core.bundle.get("esr-flowrate-reader.no-time");
        return ESRUtls.round(totalAmount / totalTime) + " " + unit.localized();
    }

    public class InputReaderBuild extends PayloadBlockBuild<Payload>{
        public float maxTime = 1f;
        public float readingTimer = 0f;
        public float totalTime = 0f;
        public float totalPowerProduced, totalPowerConsumed;
        public PayloadSeq payloads = new PayloadSeq();

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            //draw input
            for(int i = 0; i < 4; i++){
                if(blends(i)){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }

            Draw.rect(topRegion, x, y);

            Draw.z(Layer.blockOver);
            drawPayload();
        }

        @Override
        public void updateTile(){
            if(readingTimer > 0f){
                readingTimer -= Time.delta;
                totalTime += Time.delta;
                totalPowerProduced += power.graph.getPowerProduced();
                totalPowerConsumed += power.graph.getPowerNeeded();
            }

            if(moveInPayload(false)){
                consumePayload();
            }
        }

        public void consumePayload(){
            if(readingTimer > 0f){
                if(payload instanceof BuildPayload p){
                    if(p.block().hasItems) items.add(p.build.items);
                    if(p.block().hasLiquids) p.build.liquids.each((liquid, amount) -> liquids.add(liquid, amount));
                }
                payloads.add(payload.content());
            }

            payload = null;
            incinerateEffect.at(this);
            incinerateSound.at(this);
        }

        @Override
        public void buildConfiguration(Table table){
            addTimeSetting = 0f;
            table.table(Styles.black6, t -> {
                t.defaults().left();
                t.margin(6f);
                t.add("@esr-flowrate-reader.add-time");
                TextField f = t.field(String.valueOf(addTimeSetting), text -> {
                    addTimeSetting = Strings.parseFloat(text);
                }).width(120).valid(text -> Strings.canParseFloat(text) && Strings.parseFloat(text) > 0).padLeft(6f).get();
                f.setFilter(TextFieldFilter.floatsOnly);
                t.add(StatUnit.seconds.localized()).padLeft(6f);
                t.button(Icon.add, () -> {
                    configure(addTimeSetting);
                    addTimeSetting = 0f;
                    f.setText(String.valueOf(addTimeSetting));
                }).padLeft(6f);
                t.button(Icon.zoom, () -> {
                    //someDialog.show(this);
                }); //TODO dialog
                t.button(Icon.refresh, () -> configure(false)).tooltip("@esr-flowrate-reader.reset");
            });
        }

        public float getTotalSeconds(){
            return totalTime / 60f;
        }

        public float getTotalLiquids(){
            float[] total = {0f};
            liquids.each((liquid, amount) -> total[0] += amount);
            return total[0];
        }

        public float barFill(){
            return totalTime > 0f ? 1f : 0f;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return enabled;
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            return amount;
        }

        @Override
        public void handleItem(Building source, Item item){
            if(readingTimer <= 0) return;
            super.handleItem(source, item);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source){
            if(readingTimer <= 0) return;
            super.handleStack(item, amount, source);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return enabled;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            if(readingTimer <= 0) return;
            super.handleLiquid(source, liquid, amount);
        }
    }
}
