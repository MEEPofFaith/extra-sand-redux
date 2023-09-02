package extrasandredux.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;

import static arc.graphics.g2d.Draw.*;
import static arc.util.Tmp.*;
import static mindustry.Vars.*;
import static mindustry.graphics.Drawf.*;

public class ESRFx{
    public static Effect
        storageConfiged = new Effect(30f, e -> {
            color(e.color);
            alpha(e.fout() * 1);
            Fill.square(e.x, e.y, e.rotation * tilesize / 2f);
        }).followParent(true).layer(Layer.blockOver + 0.05f),

        eviscerationCharge = new Effect(150f, 1600f, e -> {
            Color[] colors = {Color.valueOf("D99F6B55"), Color.valueOf("E8D174aa"), Color.valueOf("F3E979"), Color.valueOf("ffffff")};
            float[] tscales = {1f, 0.7f, 0.5f, 0.2f};
            float[] strokes = {2f, 1.5f, 1, 0.3f};
            float[] lenscales = {1, 1.12f, 1.15f, 1.17f};

            float lightOpacity = 0.4f + (e.finpow() * 0.4f);

            color(colors[0], colors[2], 0.5f + e.finpow() * 0.5f);
            Lines.stroke(Mathf.lerp(0f, 28f, e.finpow()));
            Lines.circle(e.x, e.y, 384f * (1f - e.finpow()));

            //TODO convert to smooth drawing like moder continuous laser drawing
            for(int i = 0; i < 36; i++){
                v1.trns(i * 10f, 384f * (1 - e.finpow()));
                v2.trns(i * 10f + 10f, 384f * (1f - e.finpow()));
                light(e.x + v1.x, e.y + v1.y, e.x + v2.x, e.y + v2.y, 14f / 2f + 60f * e.finpow(), Draw.getColor(), lightOpacity + (0.2f * e.finpow()));
            }

            float fade = 1f - Mathf.curve(e.time, e.lifetime - 30f, e.lifetime);
            float grow = Mathf.curve(e.time, 0f, e.lifetime - 30f);

            for(int i = 0; i < 4; i++){
                float baseLen = (900f + (Mathf.absin(Time.time / ((i + 1f) * 2f) + Mathf.randomSeed(e.id), 0.8f, 1.5f) * (900f / 1.5f))) * 0.75f * fade;
                color(Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time / 3f + Mathf.randomSeed(e.id), 1.0f, 0.3f) / 3f));
                for(int j = 0; j < 2; j++){
                    int dir = Mathf.signs[j];
                    for(int k = 0; k < 10; k++){
                        float side = k * (360f / 10f);
                        for(int l = 0; l < 4; l++){
                            Lines.stroke((16f * 0.75f + Mathf.absin(Time.time, 0.5f, 1f)) * grow * strokes[i] * tscales[l]);
                            Lines.lineAngle(e.x, e.y, (e.rotation + 360f * e.finpow() + side) * dir, baseLen * lenscales[l], false);
                        }

                        v1.trns((e.rotation + 360f * e.finpow() + side) * dir, baseLen * 1.1f);

                        light(e.x, e.y, e.x + v1.x, e.y + v1.y, ((16f * 0.75f + Mathf.absin(Time.time, 0.5f, 1f)) * grow * strokes[i] * tscales[j]) / 2f + 60f * e.finpow(), colors[2], lightOpacity);
                    }
                }
                Draw.reset();
            }
        }),

        //[circle radius, distance]
        everythingGunSwirl = new Effect(120f, 1600f, e -> {
            float[] data = (float[])e.data;
            v1.trns(Mathf.randomSeed(e.id, 360f) + e.rotation * e.fin(), (16f + data[1]) * e.fin());
            color(e.color, Color.black, 0.25f + e.fin() * 0.75f);
            Fill.circle(e.x + v1.x, e.y + v1.y, data[0] * e.fout());
        }).layer(Layer.bullet - 0.00999f);
}
