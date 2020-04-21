/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.game;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.games.platform2d.Platform2DPlayer;
import com.bruynhuis.galago.games.platform2d.Tile;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.Animation;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.util.SpatialUtils;
import com.bruynhuis.galago.util.SpriteUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class Player extends Platform2DPlayer implements PhysicsTickListener {

    private Material material;
    private Material bulletMaterial;
    private Material shadowMaterial;
    private RigidBodyControl rbc;
    private AnimatedSprite sprite;
    private Vector3f movementDirection = new Vector3f(0, 0, 0);
    private Vector3f shootDirection = new Vector3f(0, 0, 0);
    private float walkSpeed = 0;
    private float MAX_WALK_SPEED = 3f;
    private float shootForce = 8;
    private float shootKickback = 1;
    private PointLight pointLight;
    private int initialHealth = 50;
    private Timer timer = new Timer(100);
    private Node flame;
    private ParticleEmitter flameEmitter;
    private Vector3f initialVelocityFlame;
    private ParticleEmitter dustEmitter;
    private Node staff;
    private boolean attacking;
    private boolean run;
    private float initialMotion = 10;
    private float motion = 0;

    public Player(Platform2DGame physicsGame) {
        super(physicsGame);
    }

    public void setHealth(int health) {
        this.lives = health;

    }

    @Override
    protected void init() {
        lives = initialHealth;

        //Setup start location
        String target = game.getBaseApplication().getGameSaves().getGameData().getProperties().getProperty("target");
        Vector3f pPos = null;
        if (target != null) {
            //Find the start point with target name
            for (int i = 0; i < game.getTileMap().getTiles().size(); i++) {
                Tile tile = game.getTileMap().getTiles().get(i);

                if (tile.getUid().startsWith("start")) {
                    String entry = tile.getProperties().getProperty("source");
                    System.out.println("Start with source on player: " + entry);
                    if (entry != null && entry.equals(target)) {
                        log("Player found a specific start: " + tile.getSpatial().getLocalTranslation());
                        pPos = tile.getSpatial().getLocalTranslation();
                        break;
                    }
                }
            }
        }

        if (pPos != null) {
            startPosition = pPos.clone();
        }

        material = game.getBaseApplication().getAssetManager().loadMaterial("Materials/player.j3m");
        bulletMaterial = game.getBaseApplication().getAssetManager().loadMaterial("Materials/bullet.j3m");
        shadowMaterial = game.getBaseApplication().getAssetManager().loadMaterial("Materials/shadow.j3m");

        sprite = new AnimatedSprite("player", 1.f, 1.f, 6, 6, 10);
        sprite.setMaterial(material);
        sprite.flipCoords(true);
        sprite.flipHorizontal(false);
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.showIndex(0);
        sprite.move(0, 0.12f, 0.1f);
        sprite.addAnimation(new Animation("idle", 0, 4, 10));
        sprite.addAnimation(new Animation("down", 6, 9, 12));
        sprite.addAnimation(new Animation("up", 12, 15, 12));
        sprite.addAnimation(new Animation("left", 18, 21, 12));
        sprite.addAnimation(new Animation("right", 24, 27, 12));
        sprite.addAnimation(new Animation("down-r", 6, 9, 10));
        sprite.addAnimation(new Animation("up-r", 12, 15, 10));
        sprite.addAnimation(new Animation("left-r", 18, 21, 10));
        sprite.addAnimation(new Animation("right-r", 24, 27, 10));
        playerNode.attachChild(sprite);

        //Load the staff prototype
        staff = new Node("staff");
        playerNode.attachChild(staff);
        staff.move(0, -0.2f, 0.11f);
//        staff.addControl(new RotationControl(new Vector3f(0, 0, -100)));

        Sprite staffSprite = new Sprite("staff", 1.1f, 1.2f, 6, 6, 10);
        staffSprite.setMaterial(material);
        staffSprite.flipCoords(true);
        staffSprite.flipHorizontal(false);
        staffSprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        staffSprite.showIndex(35);
        staff.attachChild(staffSprite);
        staffSprite.move(0, 0.3f, 0.101f);
        SpatialUtils.rotateTo(staff, 0, 0, -60f);

        Sprite shadow = new Sprite("shadow", 1.2f, 1.2f);
        shadow.setMaterial(shadowMaterial);
        shadow.setQueueBucket(RenderQueue.Bucket.Transparent);
        playerNode.attachChild(shadow);
        shadow.move(0, -0.1f, 0);

//        Spatial box = SpatialUtils.addBox(playerNode, 1, 1, 1);
//        SpatialUtils.addColor(box, ColorRGBA.Red, false);
        flame = (Node) game.getBaseApplication().getAssetManager().loadModel("Models/flame.j3o");
        staff.attachChild(flame);
        flame.move(0, 0.95f, 0.0f);

//        playerNode.attachChild(flame);
//        flame.move(0, 0.45f, 0.2f);
        flameEmitter = (ParticleEmitter) flame.getChild(0);
        initialVelocityFlame = flameEmitter.getParticleInfluencer().getInitialVelocity();

        Node dustNode = (Node) game.getBaseApplication().getAssetManager().loadModel("Models/dust.j3o");
        playerNode.attachChild(dustNode);
        dustNode.move(0, -0.5f, 0);
        dustEmitter = (ParticleEmitter) dustNode.getChild(0);
        dustEmitter.setParticlesPerSec(0);

        rbc = SpriteUtils.addCircleMass(playerNode, 0.4f, 1);
        rbc.setPhysicLocation(startPosition.clone());
        rbc.setGravityScale(0);

        pointLight = new PointLight(startPosition.clone(), ColorRGBA.White.mult(2f));
        pointLight.setRadius(lives);
//        pointLight.setIntersectsFrustum(true);
        game.getRootNode().addLight(pointLight);

        playerNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (pointLight != null && rbc != null) {

                    if (game.isStarted()) {
                        timer.update(tpf);
                        if (timer.finished()) {

                            if (lives <= 5) {
                                doDamage(1);
                                flameEmitter.setStartSize(0.3f * ((float) lives / 5));
                            } else {
                                lives--;
                            }
                            timer.reset();

//                            log("Health is now: " + health);
                        }

                    }

                    if (lives > 0) {
                        pointLight.setRadius(1 + (lives * 0.5f));
                        pointLight.setPosition(rbc.getPhysicLocation().add(0, 0, 1 + ((float) lives) * 0.2f));

                        initialVelocityFlame.x = -movementDirection.x;
                        initialVelocityFlame.y = 1.1f * ((float) lives / (float) initialHealth);
                        flameEmitter.getParticleInfluencer().setInitialVelocity(initialVelocityFlame);
                    } else {
                        flameEmitter.killAllParticles();
                    }

                }

                if (game.isStarted() && !game.isGameOver() && !game.isLoading()) {

                    if (run) {
                        if (motion > 0) {
                            motion -= tpf;

                        } else {
                            stopSpeed();
                        }

                    }

                    if (movementDirection.x != 0 || movementDirection.y != 0) {
                        walkSpeed += tpf * 50;
                        if (walkSpeed > MAX_WALK_SPEED) {
                            walkSpeed = MAX_WALK_SPEED;
                        }

                        if (run) {
                            game.getBaseApplication().getSoundManager().playMusic("run");
                        } else {
                            game.getBaseApplication().getSoundManager().playMusic("walk");
                        }

                        if (movementDirection.y < 0) {
                            sprite.play(run ? "down-r" : "down", true, false, true);
                            dustEmitter.setParticlesPerSec(0);
                            if (!attacking) {
                                SpatialUtils.rotateTo(staff, 0, 0f, -60f);
                            }

                        } else if (movementDirection.y > 0) {
                            sprite.play(run ? "up-r" : "up", true, false, true);
                            dustEmitter.setParticlesPerSec(20);
                            dustEmitter.getParticleInfluencer().getInitialVelocity().x = 0;
                            dustEmitter.getParticleInfluencer().getInitialVelocity().y = 0;

                            if (!attacking) {
                                SpatialUtils.rotateTo(staff, 0, 180f, -60f);
                            }

                        } else if (movementDirection.x < 0) {
                            sprite.play(run ? "left-r" : "left", true, false, true);
                            dustEmitter.setParticlesPerSec(20);
                            dustEmitter.getParticleInfluencer().getInitialVelocity().x = 2;
                            dustEmitter.getParticleInfluencer().getInitialVelocity().y = 3;

                            if (!attacking) {
                                SpatialUtils.rotateTo(staff, 0, 180f, -60f);
                            }

                        } else if (movementDirection.x > 0) {
                            sprite.play(run ? "right-r" : "right", true, false, true);
                            dustEmitter.setParticlesPerSec(20);
                            dustEmitter.getParticleInfluencer().getInitialVelocity().x = -2;
                            dustEmitter.getParticleInfluencer().getInitialVelocity().y = 3;

                            if (!attacking) {
                                SpatialUtils.rotateTo(staff, 0, 0f, -60f);
                            }

                        } else {
                            dustEmitter.setParticlesPerSec(0);

                        }

                    } else {
                        walkSpeed = 0;
                        dustEmitter.setParticlesPerSec(0);
                        sprite.play("idle", true, false, true);
                        if (run) {
                            game.getBaseApplication().getSoundManager().stopMusic("run");
                        } else {
                            game.getBaseApplication().getSoundManager().stopMusic("walk");
                        }

                        if (!attacking) {
                            SpatialUtils.rotateTo(staff, 0, 0f, -60f);
                        }

                    }

                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });

    }

    @Override
    protected float getSize() {
        return 1f;
    }

    @Override
    public Vector3f getPosition() {
        return rbc.getPhysicLocation();
    }

    @Override
    public void start() {
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(this);
        timer.start();
        sprite.play("idle", true, false, true);
        rbc.setLinearDamping(3);
    }

    @Override
    public void doDie() {
        flame.removeFromParent();
        staff.removeFromParent();
        game.getRootNode().removeLight(pointLight);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsTickListener(this);

    }

    @Override
    public void doDamage(int hits) {
        game.getBaseApplication().getSoundManager().playSound("player-hit");
        ((Game) game).getChromaticAberration().doEffect(0.1f, new Vector3f(0.015f, 0.0f, -0.015f));
        super.doDamage(hits);
        game.fireScoreChangedListener(score);
    }

    public Vector3f getMovementDirection() {
        return movementDirection;
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {

        rbc.move(movementDirection.x * tpf * walkSpeed, movementDirection.y * tpf * walkSpeed);

    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

        rbc.setPhysicRotation(0);

    }

    public void attack() {
        if (!attacking) {
            attacking = true;
            log("Do attack");

//            float startA = -60;
//            float endA = -90f;
            Vector3f fromA = new Vector3f(0f, 0f, 0f);
            Vector3f toA = new Vector3f(0f, 0f, 0f);

            if (shootDirection.x < 0) {
                fromA.z = 60;
                toA.z = 90;
                fromA.y = 0;
                toA.y = 0;

            } else if (shootDirection.x > 0) {
                fromA.z = -60;
                toA.z = -90;

            } else if (shootDirection.y > 0) {
                fromA.z = 60;
                toA.z = 0;
                fromA.y = 180;
                toA.y = 180;

            } else if (shootDirection.y < 0) {
                fromA.z = -60;
                toA.z = -180;
            }

//            log("Start Angle = " + startA);
//            log("End Angle = " + endA);
            SpatialUtils.rotateFromTo(staff, fromA, toA, 0.15f, 0f, Linear.INOUT, new TweenCallback() {
                @Override
                public void onEvent(int i, BaseTween<?> bt) {
                    shoot();
                    float xOff = 0.2f;
                    float yOff = -0.2f;

                    if (shootDirection.y > 0) {
                        xOff = 0f;
                        yOff = 0;

                    } else if (shootDirection.y < 0) {
                        xOff = 0f;
                        yOff = -0.4f;

                    }

                    SpatialUtils.moveFromToCenter(staff, 0, -0.2f, 0.11f,
                            xOff, yOff, 0.11f,
                            0.15f, 0, new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> bt) {
                            attacking = false;
                            staff.setLocalTranslation(0, -0.2f, 0.11f);
                            SpatialUtils.rotateTo(staff, 0, 0, -60);
                        }
                    }).repeatYoyo(1, 0.1f)
                            .start(game.getBaseApplication().getTweenManager());

                }
            }).start(game.getBaseApplication().getTweenManager());
        }

    }

    /**
     * Shoot a bullet in the direction the player is facing
     */
    public void shoot() {

        log("Shooting bullet");

        Sprite bullet = new Sprite("bullet", 0.3f, 0.3f);
        bullet.setMaterial(bulletMaterial);
        bullet.getChild(0).addControl(new RotationControl(new Vector3f(0, 0, -600)));
        bullet.move(0, 0, 1);

        RigidBodyControl bulletRbc = new RigidBodyControl(new CircleCollisionShape(0.2f), 1);
        bulletRbc.setGravityScale(0);
        bulletRbc.setSensor(true);
        bulletRbc.setPhysicLocation(rbc.getPhysicLocation().x + (shootDirection.x * 0.5f), rbc.getPhysicLocation().y + (shootDirection.y * 0.5f) - 0.2f);
        bullet.addControl(bulletRbc);
        game.addBullet(bulletRbc);

        bulletRbc.setLinearVelocity(shootDirection.x * shootForce, shootDirection.y * shootForce);

        PointLight light = null;

        if (lives > ((float) initialHealth / 2f)) {
            light = new PointLight(bulletRbc.getPhysicLocation().add(0, 0, 3), ColorRGBA.Yellow.mult(1.2f), 3);
            game.getRootNode().addLight(light);

            Spatial particles = game.getBaseApplication().getAssetManager().loadModel("Models/fireball.j3o");
            bullet.attachChild(particles);

            lives--;
        }

        bullet.addControl(new BulletControl((Game) game, bulletRbc, light, false));
        rbc.clearForces();
        rbc.setLinearVelocity(shootDirection.x * -shootKickback, shootDirection.y * -shootKickback);

        game.getBaseApplication().getSoundManager().playSound("fireball");

    }

    public Vector3f getShootDirection() {
        return shootDirection;
    }

    @Override
    public void close() {
        flame.removeFromParent();
        staff.removeFromParent();
        game.getRootNode().removeLight(pointLight);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsTickListener(this);
        super.close(); //To change body of generated methods, choose Tools | Templates.
    }

    public int getInitialHealth() {
        return initialHealth;
    }

    public void addHealth() {
        lives += 25f;

        if (lives > initialHealth) {
            lives = initialHealth;
        }

    }

    public void addSpeed() {
        MAX_WALK_SPEED = 5f;
        game.getBaseApplication().getSoundManager().stopMusic("walk");
        run = true;
        motion = 10;

    }

    public void stopSpeed() {
        MAX_WALK_SPEED = 3f;
        game.getBaseApplication().getSoundManager().stopMusic("run");
        run = false;
        motion = 0;

    }

    public float getMAX_WALK_SPEED() {
        return MAX_WALK_SPEED;
    }

    public void setMAX_WALK_SPEED(float speed) {
        if (speed >= 5) {
            game.getBaseApplication().getSoundManager().stopMusic("walk");
            run = true;
            motion = 10;

        }

        this.MAX_WALK_SPEED = speed;

    }

    public float getMotion() {
        return motion;
    }

    public float getInitialMotion() {
        return initialMotion;
    }

}
