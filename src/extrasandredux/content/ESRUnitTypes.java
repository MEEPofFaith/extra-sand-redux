package extrasandredux.content;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import extrasandredux.*;
import extrasandredux.gen.entities.*;
import extrasandredux.graphics.*;
import extrasandredux.type.unit.*;
import extrasandredux.ui.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.*;

public class ESRUnitTypes{

    public static UnitType
        //oof
        targetDummy,

        //oh god oh god oh god
        allWeaponsUnit;

    public static void load(){
        targetDummy = EntityRegistry.content("dummy", TargetDummyUnit.class, name -> new DummyUnitType(name){{
            drag = 0.33f;
            hideDetails = false;
            hitSize = 13f;
            engineOffset = 7f;
            engineSize = 2f;
            for(int i = 0; i < 3; i++){
                engines.add(new UnitEngine(Geometry.d4x(i) * engineOffset, Geometry.d4y(i) * engineOffset, engineSize, i * 90));
            }
        }});

        allWeaponsUnit = EntityRegistry.content("god", UnitEntity.class, name -> new UnitType(name){
            {
                alwaysUnlocked = true;
                hidden = !ExtraSandRedux.everything;
                flying = true;
                lowAltitude = true;
                mineSpeed = 10000f;
                mineTier = 10000;
                buildSpeed = 10000f;
                drag = 0.05f;
                speed = 3.55f;
                rotateSpeed = 19f;
                accel = 0.11f;
                engineOffset = 5.5f;
                hitSize = 11f;
                bounded = false;
            }

            @Override
            public void setStats(){
                super.setStats();

                stats.remove(Stat.abilities);
                stats.remove(Stat.weapons);
                stats.add(Stat.abilities, t -> t.add(ESRElements.everything()));
                stats.add(Stat.weapons, t -> t.add(ESRElements.everything()));
            }

            @Override
            public void draw(Unit unit){
                super.draw(unit);

                if(!ExtraSandRedux.everything){
                    Draw.z(Layer.overlayUI);
                    ESRDrawf.text(unit.x, unit.y, false, -1, unit.team.color, Core.bundle.get("esr-sandbox-disabled"));
                }
            }
        });
    }
}
