package extrasandredux.world.blocks.defence.turret;

import arc.*;
import arc.math.*;
import arc.math.Interp.*;
import arc.struct.*;
import arc.util.*;
import extrasandredux.util.*;
import extrasandredux.world.meta.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class EviscerationTurret extends PowerTurret{
    protected PowIn pow = Interp.pow5In;

    public EviscerationTurret(String name){
        super(name);
        ESRUtls.applySandboxDefaults(this, Category.turret);

        drawer = new DrawTurret(){
            @Override
            public void drawHeat(Turret block, TurretBuild build){
                if(build.heat <= 0.00001f || !heat.found()) return;

                float r = Interp.pow2Out.apply(build.heat);
                float g = Interp.pow3In.apply(build.heat) + ((1f - Interp.pow3In.apply(build.heat)) * 0.12f);
                float b = pow.apply(build.heat);
                float a = Interp.pow2Out.apply(build.heat);
                Tmp.c1.set(r, g, b, a);

                Drawf.additive(heat, Tmp.c1, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot(), Layer.turretHeat);
            }
        };
    }

    public void setStats(){
        super.setStats();

        stats.remove(Stat.ammo);
        stats.add(Stat.ammo, ESRStatValues.infiniteDamageAmmo(ObjectMap.of(this, shootType)));
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("pm-reload", (ChaosTurretBuild entity) -> new Bar(
            () -> Core.bundle.format("bar.esr-reload", Strings.autoFixed(Mathf.clamp(entity.reloadCounter / reload) * 100f, 2)),
            () -> entity.team.color,
            () -> Mathf.clamp(entity.reloadCounter / reload)
        ));
    }

    public class ChaosTurretBuild extends PowerTurretBuild{
        protected Bullet bullet;

        @Override
        public void updateTile(){
            super.updateTile();

            if(active()){
                heat = 1f;
                curRecoil = 1f;
                wasShooting = true;
            }
        }

        @Override
        public boolean shouldTurn(){
            return super.shouldTurn() && !active() && !charging();
        }

        @Override
        protected void updateCooling(){
            if(!active() && !charging()) super.updateCooling();
        }

        @Override
        protected void updateReload(){
            if(!active() && !charging()) super.updateReload();
        }

        @Override
        protected void handleBullet(Bullet bullet, float offsetX, float offsetY, float angleOffset){
            if(bullet != null){
                this.bullet = bullet;
            }
        }

        public boolean active(){
            return bullet != null && bullet.isAdded();
        }
    }
}
