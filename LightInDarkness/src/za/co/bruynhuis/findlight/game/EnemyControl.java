/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.game;

import aurelienribon.tweenengine.Tween;
import com.bruynhuis.galago.games.platform2d.Tile;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.util.SpriteUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class EnemyControl extends AbstractControl implements PhysicsTickListener {

    private Game game;
    private RigidBodyControl rigidBodyControl;
    private boolean destroy;
    private int health = 1;
    private float detectDistance = 6f;
    private Vector3f movementDirection;
    private float walkSpeed = 2;
    private float damageKickback = 10;
    private int damageAmount = 1;
    private AnimatedSprite animatedSprite;
    private Tween tween;
    private boolean rageAttack = false;
    private String walkSound = "spider";
    private String dieEffect = "explode-green";
    private Timer cooloffTimer;

    public EnemyControl(Game game, RigidBodyControl rigidBodyControl) {
        this.game = game;
        this.rigidBodyControl = rigidBodyControl;

    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setDetectDistance(float detectDistance) {
        this.detectDistance = detectDistance;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public void setDamageKickback(float damageKickback) {
        this.damageKickback = damageKickback;
    }

    public void setWalkSound(String walkSound) {
        this.walkSound = walkSound;
    }

    public void setDieEffect(String dieEffect) {
        this.dieEffect = dieEffect;
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
            game.getBaseApplication().getEffectManager().doEffect(dieEffect, rigidBodyControl.getPhysicLocation().clone());

            game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rigidBodyControl);
            spatial.removeFromParent();

        }

        if (game.isStarted() && !game.isGameOver() && !game.isPaused() && !destroy) {
            
            if (cooloffTimer == null) {
                cooloffTimer = new Timer(50);
                cooloffTimer.start();
            }

            cooloffTimer.update(tpf);

            if (movementDirection == null) {
                movementDirection = new Vector3f(0, 0, 0);
                game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(this);
                animatedSprite = (AnimatedSprite) spatial;

            }

            if (rigidBodyControl.getPhysicLocation().distance(game.getPlayer().getPosition()) <= detectDistance || rageAttack) {
                //Walk towards the player
                if (cooloffTimer.finished()) {
                    movementDirection = game.getPlayer().getPosition().subtract(rigidBodyControl.getPhysicLocation()).normalize();
                    animatedSprite.play("walk", true, false, true);
                    game.getBaseApplication().getSoundManager().playMusic(walkSound);
                    if (tween != null) {
                        tween.kill();
                        tween = null;
                    }
                }

            } else {
                animatedSprite.play("idle", true, false, true);
                game.getBaseApplication().getSoundManager().pauseMusic(walkSound);
                movementDirection = new Vector3f(0, 0, 0);

                if (tween == null) {
                    tween = SpriteUtils.scaleFromTo(animatedSprite, 1, 1, 0.9f, 1.1f, 1, 0.2f, Tween.INFINITY, true, 0f);
                    tween.start(game.getBaseApplication().getTweenManager());

                }
            }

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
    
    public boolean isCoolingOff() {
        return cooloffTimer != null && cooloffTimer.finished();
    }

    public void doHit() {

        if (!destroy) {
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
        cooloffTimer.reset();
        rigidBodyControl.setLinearVelocity(movementDirection.x * -damageKickback, movementDirection.y * -damageKickback);

    }

    public int getDamageAmount() {
        return damageAmount;
    }

}
