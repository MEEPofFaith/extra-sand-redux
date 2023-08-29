package extrasandredux.graphics;

import arc.graphics.*;

import static mindustry.Vars.*;

public class ESRPal{
    public static Color[]
    itemColors,
    liquidColors;

    public static void init(){
        int items = content.items().size;
        itemColors = new Color[items + 1];
        for(int i = 0; i < items; i++){
            itemColors[i] = content.item(i).color;
        }
        itemColors[items] = content.items().first().color;

        int liquids = content.liquids().size;
        liquidColors = new Color[liquids + 1];
        for(int i = 0; i < liquids; i++){
            liquidColors[i] = content.liquid(i).color;
        }
        liquidColors[liquids] = content.liquids().first().color;
    }
}
