/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.game;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.util.SpriteUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class ShootingWallControl extends AbstractControl {

    private Game game;
    private int damageAmount = 3;
    private float shootForce = 5;
    private float detectDistance = 5f;
    private Timer shootTimer = new Timer(100);
    private boolean shoot = false;
    private Vector3f shootDirection = new Vector3f(0, 0, 0);
    private float angle = 0;
    private boolean detected = false;

    public ShootingWallControl(Game game) {
        this.game = game;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {

            shootTimer.update(tpf);
            if (shootTimer.finished()) {
                shoot();
                shootTimer.reset();
            }

            angle = spatial.getLocalRotation().toAngleAxis(Vector3f.UNIT_Z) * FastMath.RAD_TO_DEG;
//            System.out.println("Wall angle = " + angle);

            detected = false;

            if (angle == 0) {
                //Shoot up
//                System.out.println("Shoot up");
                shootDirection.x = 0;
                shootDirection.y = 1;
                if (FastMath.abs(spatial.getLocalTranslation().x - game.getPlayer().getPosition().x) < 0.75f) {
                    detected = true;
                }

            } else if (angle == 180) {
                //Shoot down
//                System.out.println("Shoot down");
                shootDirection.x = 0;
                shootDirection.y = -1;
                if (FastMath.abs(spatial.getLocalTranslation().x - game.getPlayer().getPosition().x) < 0.75f) {
                    detected = true;
                }

            } else if (angle == 90) {
                //Shoot left
//                System.out.println("Shoot left");
                shootDirection.x = -1;
                shootDirection.y = 0;
                if (FastMath.abs(spatial.getLocalTranslation().y - game.getPlayer().getPosition().y) < 0.75f) {
                    detected = true;
                }

            } else if (angle == 270) {
                //Shoot right
//                System.out.println("Shoot right");
                shootDirection.x = 1;
                shootDirection.y = 0;
                if (FastMath.abs(spatial.getLocalTranslation().y - game.getPlayer().getPosition().y) < 0.75f) {
                    detected = true;
                }

            }

            if (detected && spatial.getLocalTranslation().distance(game.getPlayer().getPosition()) <= detectDistance) {
                //Walk towards the player
                if (!shoot) {
                    shootTimer.start();
                    shoot = true;
                    shootTimer.setCounterTo(100);
                }

            } else {
                shootTimer.stop();
                shoot = false;
            }

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public int getDamageAmount() {
        return damageAmount;
    }

    /**
     * Shoot a bullet in the direction the player is facing
     */
    public void shoot() {

        System.out.println("Shooting spear");

        Sprite bullet = new Sprite("bullet", 0.05f, 0.25f);
        SpriteUtils.addColor(bullet, ColorRGBA.White, true);
        bullet.move(0, 0, 0.5f);

        RigidBodyControl bulletRbc = new RigidBodyControl(new BoxCollisionShape(0.1f, 0.1f), 1);
        bulletRbc.setGravityScale(0);
        bulletRbc.setSensor(true);
        bulletRbc.setPhysicLocation(spatial.getLocalTranslation().x + shootDirection.x * 0.3f, spatial.getLocalTranslation().y + shootDirection.y * 0.3f);
        bulletRbc.setPhysicRotation(angle * FastMath.DEG_TO_RAD);
        bullet.addControl(bulletRbc);
        game.addBullet(bulletRbc);

        bulletRbc.setLinearVelocity(shootDirection.x * shootForce, shootDirection.y * shootForce);

        BulletControl bulletControl = new BulletControl(game, bulletRbc, null, true);
        bulletControl.setExplodeEffect("explode-arrow");
        bulletControl.setExplodeSound("arrow-hit");
        bullet.addControl(bulletControl);

        game.getBaseApplication().getSoundManager().playSound("arrow");

    }
}
