package extrasandredux.world.consumers;

import arc.func.*;
import mindustry.gen.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

public class ConsumeBufferedPowerDynamic extends ConsumePower{
    private final Floatf<Building> dynamicCapacity;

    public ConsumeBufferedPowerDynamic(Floatf<Building> dynamicCapacity){
        super(0, 1, true);
        this.dynamicCapacity = dynamicCapacity;

        update = true;
    }

    @Override
    public boolean ignore(){
        return false;
    }

    @Override
    public void update(Building build){
        capacity = dynamicCapacity.get(build);
    }

    @Override
    public float efficiency(Building build){
        return 1f;
    }

    @Override
    public void display(Stats stats){
        //Power capacity varies, don't display
    }

    public float getPowerCapacity(Building build){
        return dynamicCapacity.get(build);
    }
}
