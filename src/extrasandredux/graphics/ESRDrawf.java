package extrasandredux.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.ui.*;

public class ESRDrawf{
    public static void setStrobeColor(){
        Draw.color(applyStrobeHue(Tmp.c1.set(Color.red)));
    }

    public static Color applyStrobeHue(Color color){
        float speed = Core.settings.getInt("esr-strobespeed", 3) / 2f;
        color.shiftHue(Time.time * speed);
        return color;
    }

    public static float text(float x, float y, Color color, CharSequence text){
        return text(x, y, true, -1, color, text);
    }

    public static float text(float x, float y, boolean underline, float maxWidth, Color color, CharSequence text){
        Font font = Fonts.outline;
        GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        if(maxWidth <= 0){
            font.getData().setScale(1f / 3f);
            layout.setText(font, text);
        }else{
            font.getData().setScale(1f);
            layout.setText(font, text);
            font.getData().setScale(Math.min(1f / 3f, maxWidth / layout.width));
            layout.setText(font, text);
        }

        font.setColor(color);
        font.draw(text, x, y + (underline ? layout.height + 1 : layout.height / 2f), Align.center);
        if(underline){
            y -= 1f;
            Lines.stroke(2f, Color.darkGray);
            Lines.line(x - layout.width / 2f - 2f, y, x + layout.width / 2f + 1.5f, y);
            Lines.stroke(1f, color);
            Lines.line(x - layout.width / 2f - 2f, y, x + layout.width / 2f + 1.5f, y);
        }

        float width = layout.width;

        font.setUseIntegerPositions(ints);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);

        return width;
    }
}
