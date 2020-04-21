/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.game;

import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.util.Timer;
import com.jme3.light.PointLight;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class BulletControl extends AbstractControl {

    private Game game;
    private RigidBodyControl rigidBodyControl;
    private boolean destroy;
    private PointLight pointLight;
    private boolean enemyBullet;
    private Timer bulletTimer;
    private String explodeSound = "bullet-hit";
    private String explodeEffect = "explode";
    private boolean playEffect = true;

    public BulletControl(Game game, RigidBodyControl rigidBodyControl, PointLight pointLight, boolean enemyBullet) {
        this.game = game;
        this.rigidBodyControl = rigidBodyControl;
        this.pointLight = pointLight;
        this.enemyBullet = enemyBullet;

        if (!this.enemyBullet) {
            bulletTimer = new Timer(80);
            bulletTimer.start();

        }
    }

    public void setExplodeSound(String explodeSound) {
        this.explodeSound = explodeSound;
    }

    public void setExplodeEffect(String explodeEffect) {
        this.explodeEffect = explodeEffect;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (destroy) {

            //Make an explosion
            if (playEffect) {
                game.getBaseApplication().getEffectManager().doEffect(explodeEffect, rigidBodyControl.getPhysicLocation().clone());
                game.getBaseApplication().getSoundManager().playSound(explodeSound);
            }

//            ((Game) player.getGame()).getCameraShaker().shake(CameraShaker.LARGE_AMOUNT, 50);
//            ((Game) player.getGame()).getChromaticAberration().doEffect(0.1f, new Vector3f(0.015f, 0.0f, -0.015f));
//            Vector3f pos = player.getGame().getBaseApplication().getCamera().getScreenCoordinates(rigidBodyControl.getPhysicLocation());
//            ((Game) player.getGame()).getShockwaveFilter().doEffect(2.2f, new Vector2f(pos.x, pos.y));
            game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rigidBodyControl);
            spatial.removeFromParent();

            if (pointLight != null) {
                game.getRootNode().removeLight(pointLight);

            }

        }

        if (pointLight != null) {
            pointLight.setPosition(rigidBodyControl.getPhysicLocation().add(0, 0f, 2));
//            System.out.println("Bullet Pos: " + pointLight.getPosition());

        }

        if (bulletTimer != null) {
            bulletTimer.update(tpf);
            if (bulletTimer.finished()) {
                playEffect = false;
                doDestroy();
                bulletTimer.stop();
            }

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public void doDestroy() {
        if (!destroy) {
            destroy = true;
        }
    }

    public boolean isEnemyBullet() {
        return enemyBullet;
    }

}
