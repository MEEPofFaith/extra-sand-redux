package extrasandredux.entities.comp;

import arc.util.*;
import ent.anno.Annotations.*;
import extrasandredux.gen.entities.*;
import extrasandredux.world.blocks.defence.TargetDummyBase.*;
import mindustry.gen.*;

@EntityComponent
@EntityDef({TargetDummyUnitc.class, Unitc.class})
abstract class TargetDummyUnitComp implements Unitc, Healthc{
    public @Nullable Building building;

    @Override
    public void update(){
        if(building == null || (!building.isPayload() && !building.isValid())){
            Call.unitDespawn(self()); //Don't despawn even if the building is on another team
        }
    }

    @Override
    public void rawDamage(float amount){
        ((TargetDummyBaseBuild)building).dummyHit(amount);
    }
}
