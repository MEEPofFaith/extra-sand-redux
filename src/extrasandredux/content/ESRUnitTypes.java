package extrasandredux.content;

import arc.math.geom.*;
import extrasandredux.gen.entities.*;
import extrasandredux.type.unit.*;
import mindustry.type.*;

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
            hitSize = 52f / 4f;
            engineOffset = 7f;
            engineSize = 2f;
            for(int i = 0; i < 3; i++){
                engines.add(new UnitEngine(Geometry.d4x(i) * engineOffset, Geometry.d4y(i) * engineOffset, engineSize, i * 90));
            }
        }});
    }
}
