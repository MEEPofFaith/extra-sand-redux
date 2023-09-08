package extrasandredux.world.draw;

import arc.graphics.g2d.*;
import extrasandredux.graphics.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawStrobe extends DrawRegion{
    public DrawStrobe(String suffix){
        super(suffix);

        drawPlan = false;
    }

    public DrawStrobe(){
        this("-strobe");
    }

    @Override
    public void draw(Building build){
        ESRDrawf.setStrobeColor();
        super.draw(build);
        Draw.color();
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{};
    }
}
