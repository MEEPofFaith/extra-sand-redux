package extrasandredux.ui;

import arc.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import extrasandredux.world.blocks.storage.FlowrateVoid.*;
import extrasandredux.world.blocks.storage.FlowrateVoid.FlowrateVoidBuild.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class FlowrateVoidDialog extends BaseDialog{
    private static final int cols = mobile ? 1 : 3;
    private final Table info = new Table();
    private FlowrateVoidBuild build;
    private int col = 0;

    public FlowrateVoidDialog(){
        super("@esr-flowrate-reader.title");
        shouldPause = false;
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);

        cont.pane(all -> {
            all.add(info).grow();
        }).grow();

        Events.on(GameOverEvent.class, e -> hide());
    }

    public void show(FlowrateVoidBuild build){
        this.build = build;
        show();
    }

    public void rebuild(){
        if(build == null) return;
        info.clear();
        buildItems();
        buildLiquids();
        buildPower();
        buildPayloads();
    }

    private void buildItems(){
        if(build.items.empty()) return;

        ESRElements.divider(info, "@content.item.name", Pal.accent);
        info.table(items -> {
            items.table(t -> {
                t.add(bundle.format("esr-flowrate-reader.total", build.items.total())).left();
                t.add(perSec(build.items.total())).padLeft(24);
            }).colspan(cols).left().row();

            col = 0;
            build.items.each((item, amount) -> {
                if(amount == 0) return;

                items.table(Styles.grayPanel, i -> {
                    i.image(item.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                    i.table(info -> {
                        info.left();
                        info.add(item.localizedName).left().bottom().wrap().growX().row();
                        info.add(bundle.format("esr-flowrate-reader.total", amount)).left().top();
                    }).left().growX();
                    i.add(perSec(amount)).right().pad(10f);
                }).uniformX().growX().pad(5);

                if(++col == cols){
                    items.row();
                    col = 0;
                }
            });

            if(col != 0){
                for(int i = col; i < cols; i++){
                    items.image().uniformX().growX().pad(5).color(Color.clear);
                }
            }
        }).growX().row();
    }

    private void buildLiquids(){
        float totalAmount = build.getTotalLiquids();
        if((totalAmount <= 0.01f)) return;

        ESRElements.divider(info, "@content.liquid.name", Pal.accent);
        info.table(fluids -> {
            fluids.table(t -> {
                t.add(bundle.format("esr-flowrate-reader.total", StatValues.fixValue(totalAmount))).left();
                t.add(perSec(totalAmount)).padLeft(24);
            }).colspan(cols).left().row();

            col = 0;
            build.liquids.each((liquid, amount) -> {
                if(amount == 0) return;

                fluids.table(Styles.grayPanel, l -> {
                    l.image(liquid.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                    l.table(info -> {
                        info.left();
                        info.add(liquid.localizedName).left().bottom().wrap().growX().row();
                        info.add(bundle.format("esr-flowrate-reader.total", StatValues.fixValue(amount))).left().top();
                    }).left().growX();
                    l.add(perSec(amount)).right().pad(10f);
                }).uniformX().growX().pad(5);

                if(++col == cols){
                    fluids.row();
                    col = 0;
                }
            });

            if(col != 0){
                for(int i = col; i < cols; i++){
                    fluids.image().uniformX().growX().pad(5).color(Color.clear);
                }
            }
        }).growX().left().row();
    }

    private void buildPower(){
        if((build.totalPowerProduced + build.totalPowerConsumed + build.totalPowerTransported) <= 0.01f) return;

        ESRElements.divider(info, "@bar.power", Pal.accent);
        info.table(Styles.grayPanel, power -> {
            power.left();
            power.defaults().left();

            power.add(bundle.format("esr-flowrate-reader.powerproduced", StatValues.fixValue(build.totalPowerProduced)));
            power.add(perSec(build.totalPowerProduced)).pad(10f).row();
            power.add(bundle.format("esr-flowrate-reader.powerconsumed", StatValues.fixValue(build.totalPowerConsumed)));
            power.add(perSec(build.totalPowerConsumed)).pad(10f).row();
            float net = build.totalPowerProduced - build.totalPowerConsumed;
            power.add(bundle.format("esr-flowrate-reader.powernet", StatValues.fixValue(net)));
            power.add((net > 0 ? "[stat]+" : "[negstat]") + StatValues.fixValue(net / build.totalTime * 60f) + StatUnit.perSecond.localized()).pad(10f).row();

            if(build.totalPowerTransported > 0.01f){
                power.add(bundle.format("esr-flowrate-reader.powertransported", StatValues.fixValue(build.totalPowerTransported)));
                power.add(perSec(build.totalPowerTransported)).pad(10f);
            }
        }).growX().left().pad(5).row();
    }

    private void buildPayloads(){
        if(!build.totalPayloads.any()) return;

        ESRElements.divider(info, "@esr-flowrate-reader.payloads", Pal.accent);
        info.table(payloads -> {
            payloads.table(t -> {
                t.add(bundle.format("esr-flowrate-reader.total", build.totalPayloads.total())).left();
                t.add(perSec(build.totalPayloads.total())).padLeft(24);
            }).colspan(cols).left().row();

            col = 0;
            ObjectIntMap<UnlockableContent> payloadMap = Reflect.get(build.totalPayloads, "payloads");
            Seq<UnlockableContent> keys = payloadMap.keys().toArray();
            keys.sort((u1, u2) -> {
                if(u1.getContentType() == u2.getContentType()){
                    return u1.id - u2.id;
                }else{
                    return u1.getContentType().ordinal() - u2.getContentType().ordinal();
                }
            });

            for(UnlockableContent content : keys){
                int amount = payloadMap.get(content);
                payloads.table(Styles.grayPanel, p -> {
                    p.top();
                    p.image(content.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                    p.table(info -> {
                        info.add(content.localizedName).left().bottom().wrap().growX().row();
                        info.add(bundle.format("esr-flowrate-reader.total", amount)).left().top();
                    }).left().growX();
                    p.add(perSec(amount)).pad(10f);

                    if(content instanceof Block block && build.payloadData.containsKey(block)){
                        p.row();
                        p.table(d -> {
                            PayloadInputData data = build.payloadData.get(block);
                            data.eachItem((i, a) -> {
                                d.image(i.uiIcon).size(32).pad(5).left().scaling(Scaling.fit);
                                d.table(t -> {
                                    t.left();
                                    t.add(i.localizedName).left().wrap().growX().row();
                                    t.add(bundle.format("esr-flowrate-reader.total", StatValues.fixValue(a))).left();
                                }).growX();
                                d.add(perSec(a)).right().growX().labelAlign(Align.right).row();
                            });
                            data.eachLiquid((l, a) -> {
                                d.image(l.uiIcon).size(32).pad(5).left().scaling(Scaling.fit);
                                d.table(t -> {
                                    t.left();
                                    t.add(l.localizedName).left().wrap().growX().row();
                                    t.add(bundle.format("esr-flowrate-reader.total", StatValues.fixValue(a))).left();
                                }).growX();
                                d.add(perSec(a)).right().growX().labelAlign(Align.right).row();
                            });
                            if(data.hasPower()){
                                d.image(Icon.power).size(32).pad(5).left().scaling(Scaling.fit).color(Pal.accent);
                                d.table(t -> {
                                    t.left();
                                    t.add("@bar.power").left().wrap().growX().row();
                                    t.add(bundle.format("esr-flowrate-reader.total", StatValues.fixValue(data.power))).left();
                                }).growX();
                                d.add(perSec(data.power)).right();
                            }
                        }).colspan(3).left().growX().pad(10f).padLeft(24);
                    }
                }).uniformX().growX().fill().pad(5).top();

                if(++col == cols){
                    payloads.row();
                    col = 0;
                }
            }

            if(col != 0){
                for(int i = col; i < cols; i++){
                    payloads.image().uniformX().growX().pad(5).color(Color.clear);
                }
            }
        }).growX();
    }

    private String perSec(float value){
        return "[stat]" + StatValues.fixValue(value / build.totalTime * 60f) + StatUnit.perSecond.localized();
    }
}
