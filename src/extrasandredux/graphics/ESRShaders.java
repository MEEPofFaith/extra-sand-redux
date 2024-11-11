package extrasandredux.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.gl.*;
import arc.util.*;
import mindustry.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class ESRShaders{
    public static ESRSpaceShader ESRSpaceShader;

    public static void init(){
        ESRSpaceShader = new ESRSpaceShader("esrspace");
    }

    public static class ESRSpaceShader extends ESRLoadShader{
        Texture texture;

        public ESRSpaceShader(String frag){
            super("screenspace", frag);
        }

        @Override
        public void apply(){
            if(texture == null){
                texture = new Texture(Vars.tree.get("shaders/esr-space.png"));
                texture.setFilter(TextureFilter.linear);
                texture.setWrap(TextureWrap.repeat);
            }

            setUniformf("u_campos", Core.camera.position.x, Core.camera.position.y);
            setUniformf("u_ccampos", Core.camera.position);
            setUniformf("u_resolution", Core.graphics.getWidth(), Core.graphics.getHeight());
            setUniformf("u_time", Time.time);

            texture.bind(1);
            renderer.effectBuffer.getTexture().bind(0);

            setUniformi("u_stars", 1);
        }
    }

    public static class ESRLoadShader extends Shader{
        public ESRLoadShader(String vert, String frag){
            super(
                files.internal("shaders/" + vert + ".vert"),
                tree.get("shaders/" + frag + ".frag")
            );
        }

        public ESRLoadShader(String frag){
            this("default", frag);
        }
    }
}
