/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.game;

import aurelienribon.tweenengine.Tween;
import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.control.camera.CameraShaker;
import com.bruynhuis.galago.games.platform2d.Tile;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.util.SpriteUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class ShootingEnemyControl extends AbstractControl implements PhysicsTickListener {

    private Game game;
    private RigidBodyControl rigidBodyControl;
    private boolean destroy;
    private int health = 5;
    private float detectDistance = 5f;
    private Vector3f movementDirection;
    private float walkSpeed = 0.5f;
    private float damageKickback = 8;
    private int damageAmount = 3;
    private float shootForce = 4;
    private AnimatedSprite animatedSprite;
    private Tween tween;
    private Timer shootTimer = new Timer(200);
    private Timer shakeTimer = new Timer(20f);
    private boolean shoot = false;
    private boolean walking = false;
    private boolean rageAttack = false;
    private String walkSound = "giant-walk";

    public ShootingEnemyControl(Game game, RigidBodyControl rigidBodyControl) {
        this.game = game;
        this.rigidBodyControl = rigidBodyControl;

    }

    @Override
    protected void controlUpdate(float tpf) {

        if (destroy) {

            Tile tile = game.getTile(animatedSprite);
            if (tile != null) {
                String uuid = tile.getProperties().getProperty("uuid");
                game.getBaseApplication().getGameSaves().getGameData().getProperties().setProperty("enemy-" + uuid, tile.getProperties().getProperty("uuid"));
                game.removeTile(tile);
            }

            game.getBaseApplication().getSoundManager().stopMusic(walkSound);

            game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsTickListener(this);

            //Make an explosion
            game.getBaseApplication().getEffectManager().doEffect("explode-red", rigidBodyControl.getPhysicLocation().clone());

            game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rigidBodyControl);
            spatial.removeFromParent();

        }

        if (game.isStarted() && !game.isGameOver() && !game.isPaused() && !destroy) {

            shootTimer.update(tpf);
            if (shootTimer.finished()) {
                shoot();
                shootTimer.reset();
            }

            shakeTimer.update(tpf);
            if (shakeTimer.finished()) {
                game.getCameraShaker().shake(CameraShaker.LARGE_AMOUNT, 50);
                shakeTimer.reset();
            }

            if (movementDirection == null) {
                movementDirection = new Vector3f(0, 0, 0);
                game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(this);
                animatedSprite = (AnimatedSprite) spatial;

            }

            if (rigidBodyControl.getPhysicLocation().distance(game.getPlayer().getPosition()) <= detectDistance || rageAttack) {
                //Walk towards the player
                movementDirection = game.getPlayer().getPosition().subtract(rigidBodyControl.getPhysicLocation()).normalize();
                animatedSprite.play("walk", true, false, true);
                game.getBaseApplication().getSoundManager().playMusic(walkSound);

                if (tween != null) {
                    tween.kill();
                    tween = null;
                }

                if (!shoot) {
                    shootTimer.start();
                    shoot = true;
                }

                if (!walking) {
                    shakeTimer.start();
                    walking = true;

                }

            } else {
                game.getBaseApplication().getSoundManager().pauseMusic(walkSound);

                animatedSprite.play("idle", true, false, true);
                movementDirection = new Vector3f(0, 0, 0);

                if (tween == null) {
                    tween = SpriteUtils.scaleFromTo(animatedSprite, 1, 1, 0.9f, 1.1f, 1, 0.2f, Tween.INFINITY, true, 0f);
                    tween.start(game.getBaseApplication().getTweenManager());

                }

                shootTimer.stop();
                shoot = false;
                shakeTimer.stop();
                walking = false;
            }

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public void doHit() {

        if (!destroy) {
            game.getBaseApplication().getSoundManager().playSound("enemy-hit");
            rageAttack = true;
            health = health - 1;
            if (health <= 0) {
                destroy = true;

            }
        }
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        rigidBodyControl.move(movementDirection.x * tpf * walkSpeed, movementDirection.y * tpf * walkSpeed);
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        rigidBodyControl.setPhysicRotation(0);

    }

    public void dealDamageToPlayer() {
        rigidBodyControl.setLinearVelocity(movementDirection.x * -damageKickback, movementDirection.y * -damageKickback);

    }

    public int getDamageAmount() {
        return damageAmount;
    }

    /**
     * Shoot a bullet in the direction the player is facing
     */
    public void shoot() {

        System.out.println("Shooting bullet");

        Sprite bullet = new Sprite("bullet", 0.3f, 0.3f);
        bullet.setMaterial(game.getBaseApplication().getAssetManager().loadMaterial("Materials/bullet.j3m"));
        bullet.getChild(0).addControl(new RotationControl(new Vector3f(0, 0, -600)));
        bullet.move(0, 0, 1f);

        RigidBodyControl bulletRbc = new RigidBodyControl(new CircleCollisionShape(0.3f), 1);
        bulletRbc.setGravityScale(0);
        bulletRbc.setSensor(true);
        bulletRbc.setPhysicLocation(rigidBodyControl.getPhysicLocation().x, rigidBodyControl.getPhysicLocation().y - 0.2f);
        bullet.addControl(bulletRbc);
        game.addBullet(bulletRbc);

        bulletRbc.setLinearVelocity(movementDirection.x * shootForce, movementDirection.y * shootForce);

        Spatial particles = game.getBaseApplication().getAssetManager().loadModel("Models/fireball-enemy.j3o");
        bullet.attachChild(particles);

        bullet.addControl(new BulletControl(game, bulletRbc, null, true));

        game.getBaseApplication().getSoundManager().playSound("fireball-enemy");

    }
}
