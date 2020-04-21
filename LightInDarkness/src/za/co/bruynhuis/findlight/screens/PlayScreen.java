/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.games.platform2d.Platform2DGameListener;
import com.bruynhuis.galago.games.platform2d.Tile;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.field.ProgressBar;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import za.co.bruynhuis.findlight.MainApplication;
import za.co.bruynhuis.findlight.game.BulletControl;
import za.co.bruynhuis.findlight.game.ChestControl;
import za.co.bruynhuis.findlight.game.EnemyControl;
import za.co.bruynhuis.findlight.game.Game;
import za.co.bruynhuis.findlight.game.PickupFireControl;
import za.co.bruynhuis.findlight.game.PickupSpeedControl;
import za.co.bruynhuis.findlight.game.Player;
import za.co.bruynhuis.findlight.game.ShootingEnemyControl;
import za.co.bruynhuis.findlight.ui.MessageCallback;

/**
 *
 * @author NideBruyn
 */
public class PlayScreen extends AbstractScreen implements Platform2DGameListener, KeyboardControlListener {

    public static final String NAME = "PlayScreen";
    private MainApplication mainApplication;
    private Label speedLabel;
    private Label healthLabel;
    private ProgressBar healthProgress;
    private ProgressBar speedProgress;

    private KeyboardControlInputListener keyboardControlInputListener;
    private Game game;
    private Player player;
    private String levelName;
    private boolean test;
    private boolean left, right, up, down;
    private boolean fireBullet;
    private Timer shootTimer = new Timer(20);
    private int playerDamage = 0;
    private String targetLevel;
    private boolean gameover;
    private boolean win;

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    @Override
    protected void init() {

        mainApplication = (MainApplication) baseApplication;

        healthLabel = new Label(hudPanel, "Health", 22, 100, 20);
        healthLabel.leftTop(10, 10);
        
        healthProgress = new ProgressBar(hudPanel, "Interface/progress-border.png", "Interface/progress-health.png", 256, 32);
        healthProgress.leftTop(120, 10);
        hudPanel.add(healthProgress);
        healthProgress.setProgress(1);

        speedLabel = new Label(hudPanel, "Motion", 22, 100, 20);
        speedLabel.rightTop(10, 10);
        
        speedProgress = new ProgressBar(hudPanel, "Interface/progress-border.png", "Interface/progress-speed.png", 256, 32);
        speedProgress.rightTop(120, 10);
        hudPanel.add(speedProgress);
        speedProgress.setProgress(1);

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);

    }

    @Override
    protected void load() {

//        baseApplication.getViewPort().setBackgroundColor(ColorUtils.rgb(25, 21, 19));
        left = false;
        right = false;
        up = false;
        down = false;
        fireBullet = false;
        playerDamage = 0;
        targetLevel = null;
        gameover = false;
        win = false;

        game = new Game((MainApplication) baseApplication, rootNode);

        log("Loading level " + levelName);

        if (test) {
            game.test(levelName);
        } else {
            game.play(levelName);
        }

        game.load();

        player = new Player(game);
        player.load();
        game.addGameListener(this);

        //Check for saved health
        if (baseApplication.getGameSaves().getGameData().getProperties().getProperty("health") != null) {
            int savedHealth = Integer.parseInt(baseApplication.getGameSaves().getGameData().getProperties().getProperty("health"));
            player.setHealth(savedHealth);
            log("Setting saved health: " + savedHealth);

        }
        if (baseApplication.getGameSaves().getGameData().getProperties().getProperty("speed") != null) {
            float savedSpeed = Float.parseFloat(baseApplication.getGameSaves().getGameData().getProperties().getProperty("speed"));
            player.setMAX_WALK_SPEED(savedSpeed);
            log("Setting saved speed: " + savedSpeed);
        }

        ((MainApplication) baseApplication).setCameraDistanceFrustrum(MainApplication.CAMERA_HEIGHT - 1.5f);
        camera.setLocation(new Vector3f(11.5f, 6.5f, MainApplication.CAMERA_HEIGHT));

        doScoreChanged(0);
    }
    
    

    @Override
    protected void show() {

        if (test) {
            setPreviousScreen(EditScreen.NAME);
        } else {
            setPreviousScreen(MenuScreen.NAME);
        }

        updateProgressBars();
        

        game.start(player);
        keyboardControlInputListener.registerWithInput(inputManager);

//        showMessage("BEGIN LEVEL", new MessageCallback() {
//            @Override
//            public void done() {
//                game.start(player);
//                keyboardControlInputListener.registerWithInput(inputManager);
//            }
//        });
    }

    @Override
    protected void exit() {
        if (!gameover && !win) {
            baseApplication.getGameSaves().getGameData().getProperties().setProperty("health", player.getLives() + "");
            baseApplication.getGameSaves().getGameData().getProperties().setProperty("speed", player.getMAX_WALK_SPEED() + "");
            baseApplication.getGameSaves().save();
        }

        keyboardControlInputListener.unregisterInput();
        game.close();

    }

    @Override
    protected void pause() {
    }

    @Override
    public void doGameOver() {
        log("Game over");
        baseApplication.getSoundManager().playSound("gameover");

        gameover = true;
        if (test) {
            showScreen(EditScreen.NAME);
        } else {
            showScreen(GameoverScreen.NAME);
        }

//        showMessage("Level Failed", new MessageCallback() {
//            @Override
//            public void done() {
//                showScreen(NAME);
//            }
//        });
    }

    @Override
    public void doGameCompleted() {
        log("You won the game");
        baseApplication.getSoundManager().playSound("win");

        win = true;

        if (test) {
            showScreen(EditScreen.NAME);

        } else {
            showScreen(WinScreen.NAME);

        }

    }

    @Override
    public void doScoreChanged(int score) {

    }

    @Override
    public void doCollisionPlayerWithTerrain(Spatial collided, Spatial collider) {
        //Nothing to do when colliding with walls

    }

    @Override
    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithStatic(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithTerrain(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider) {
//        log("Found pickup");
        if (collided.getControl(PickupFireControl.class) != null) {
            collided.getControl(PickupFireControl.class).doPickup();

        } else if (collided.getControl(PickupSpeedControl.class) != null) {
            collided.getControl(PickupSpeedControl.class).doPickup();

        } else if (targetLevel == null && !win && !gameover && !game.hasEnemy()) {

            //Handle the door exit
            Tile tile = game.getTileAtPosition(collided.getLocalTranslation());
            if (tile != null && tile.getProperties().getProperty("level") != null) {
//                log("Entering door: " + tile.getProperties().getProperty("level"));
//                log("Target start: " + tile.getProperties().getProperty("target"));
                targetLevel = tile.getProperties().getProperty("level");
                baseApplication.getGameSaves().getGameData().getProperties().setProperty("level", tile.getProperties().getProperty("level"));
                baseApplication.getGameSaves().getGameData().getProperties().setProperty("target", tile.getProperties().getProperty("target"));

            } else if (tile != null && tile.getProperties().getProperty("win") != null) {
                game.doLevelCompleted();

            }

        }

    }

    @Override
    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider) {

        if (collided.getControl(EnemyControl.class) != null) {
//            log("Deal damage to player");

            if (collided.getControl(EnemyControl.class).isCoolingOff()) {
                collided.getControl(EnemyControl.class).dealDamageToPlayer();
                playerDamage = playerDamage + collided.getControl(EnemyControl.class).getDamageAmount();
            }

        } else if (collided.getControl(ShootingEnemyControl.class) != null) {
//            log("Deal damage to player");
            collided.getControl(ShootingEnemyControl.class).dealDamageToPlayer();
            playerDamage = playerDamage + collided.getControl(ShootingEnemyControl.class).getDamageAmount();

        }

    }

    @Override
    public void doCollisionPlayerWithBullet(Spatial collided, Spatial collider) {
        if (collided.getControl(BulletControl.class) != null && collided.getControl(BulletControl.class).isEnemyBullet()) {

            playerDamage += 1f;

            collided.getControl(BulletControl.class).doDestroy();
            RigidBodyControl rigidBodyControl = collided.getControl(RigidBodyControl.class);
            if (rigidBodyControl != null) {
                Vector3f pos = player.getGame().getBaseApplication().getCamera().getScreenCoordinates(rigidBodyControl.getPhysicLocation());
                game.getShockwaveFilter().doEffect(2.2f, new Vector2f(pos.x, pos.y));
            }

        }

    }

    @Override
    public void doCollisionObstacleWithBullet(Spatial collided, Spatial collider) {

        if (collider.getControl(ChestControl.class) != null) {
            collider.getControl(ChestControl.class).doHit();

        }

        if (collided.getControl(BulletControl.class) != null) {
            collided.getControl(BulletControl.class).doDestroy();

        }

    }

    @Override
    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider) {

        if (collided.getControl(BulletControl.class) != null && !collided.getControl(BulletControl.class).isEnemyBullet()) {

            if (collider.getControl(EnemyControl.class) != null) {
                collider.getControl(EnemyControl.class).doHit();

            } else if (collider.getControl(ShootingEnemyControl.class) != null) {
                collider.getControl(ShootingEnemyControl.class).doHit();
            }

            collided.getControl(BulletControl.class).doDestroy();

        }
    }

    @Override
    public void doCollisionBulletWithPickup(Spatial collided, Spatial collider) {
        if (collider.getControl(BulletControl.class) != null) {
            collider.getControl(BulletControl.class).doDestroy();
        }
    }

    @Override
    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithObstacle(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithPickup(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionTerrainWithBullet(Spatial collided, Spatial collider) {
        if (collided.getControl(BulletControl.class) != null) {
            collided.getControl(BulletControl.class).doDestroy();
        }
    }

    @Override
    public void update(float tpf) {
        if (isActive() && game.isStarted() && !game.isPaused() && !game.isGameOver()) {

            if (fireBullet) {
                shootTimer.update(tpf);
                if (shootTimer.finished()) {
                    player.attack();
                    shootTimer.reset();
                }

            }

//            healthLabel.setText("Health: " + player.getLives() + "/" + player.getInitialHealth());
            updateProgressBars();

            if (playerDamage > 0) {
                player.doDamage(playerDamage);
                playerDamage = 0;

            }

            if (targetLevel != null) {
                setLevelName("ld46-" + targetLevel + EditScreen.FILE_EXT);
                setTest(test);
                showScreen(PlayScreen.NAME);
                targetLevel = null;
            }
        }
    }

    @Override
    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {

            if (keyboardControlEvent.isDown()) {
//                log("moving down");
                if (keyboardControlEvent.isSecondSet()) {
                    player.getMovementDirection().y = keyboardControlEvent.isKeyDown() ? -1 : 0;
                    if (player.getMovementDirection().y == 0) {
                        player.getMovementDirection().y = up ? 1 : 0;
                    }
                    down = keyboardControlEvent.isKeyDown();
                }

                if (!keyboardControlEvent.isSecondSet()) {
                    player.getShootDirection().x = 0;
                    player.getShootDirection().y = -1f;
                    fireBullet = keyboardControlEvent.isKeyDown();
                    shootTimer.reset();
                    shootTimer.setCounterTo(20);

                }

            }
            if (keyboardControlEvent.isUp()) {
//                log("moving up");
                if (keyboardControlEvent.isSecondSet()) {
                    player.getMovementDirection().y = keyboardControlEvent.isKeyDown() ? 1 : 0;
                    if (player.getMovementDirection().y == 0) {
                        player.getMovementDirection().y = down ? -1 : 0;
                    }
                    up = keyboardControlEvent.isKeyDown();
                }

                if (!keyboardControlEvent.isSecondSet()) {
                    player.getShootDirection().x = 0;
                    player.getShootDirection().y = 1f;
                    fireBullet = keyboardControlEvent.isKeyDown();
                    shootTimer.reset();
                    shootTimer.setCounterTo(20);

                }

            }

            if (keyboardControlEvent.isLeft()) {
//                log("moving left");
                if (keyboardControlEvent.isSecondSet()) {
                    player.getMovementDirection().x = keyboardControlEvent.isKeyDown() ? -1 : 0;
                    if (player.getMovementDirection().x == 0) {
                        player.getMovementDirection().x = right ? 1 : 0;
                    }

                    left = keyboardControlEvent.isKeyDown();
                }

                if (!keyboardControlEvent.isSecondSet()) {
                    player.getShootDirection().x = -1;
                    player.getShootDirection().y = 0f;
                    fireBullet = keyboardControlEvent.isKeyDown();
                    shootTimer.reset();
                    shootTimer.setCounterTo(20);

                }

            }
            if (keyboardControlEvent.isRight()) {
//                log("moving right");
                if (keyboardControlEvent.isSecondSet()) {
                    player.getMovementDirection().x = keyboardControlEvent.isKeyDown() ? 1 : 0;
                    if (player.getMovementDirection().x == 0) {
                        player.getMovementDirection().x = left ? -1 : 0;
                    }
                    right = keyboardControlEvent.isKeyDown();
                }

                if (!keyboardControlEvent.isSecondSet()) {
                    player.getShootDirection().x = 1;
                    player.getShootDirection().y = 0f;
                    fireBullet = keyboardControlEvent.isKeyDown();
                    shootTimer.reset();
                    shootTimer.setCounterTo(20);

                }
            }

            if (keyboardControlEvent.isButton2()) {
//                log("Fire bullet");
//                fireBullet = keyboardControlEvent.isKeyDown();
//                shootTimer.reset();
//                shootTimer.setCounterTo(10);

            }
        }

    }

    private void showMessage(String text, final MessageCallback callback) {
        final Label label = new Label(hudPanel, text, 26, 480, 50);
        label.setTextColor(ColorRGBA.White);
        label.setAlignment(TextAlign.CENTER);
        label.moveFromToCenter(0, -500, 0, -260, 1.5f, 0.25f, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {

                label.fadeFromTo(1, 0, 0.5f, 0.5f, new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> bt) {
                        label.remove();
                        hudPanel.getWidgets().remove(label);
                        if (callback != null) {
                            callback.done();
                        }
                    }
                });

            }
        });

    }

    private void updateProgressBars() {
        if (player.getLives() > 0) {
            healthProgress.setProgress((float)player.getLives()/(float)player.getInitialHealth());
        } else {
            healthProgress.setProgress(0);
        }
        
        if (player.getMotion() > 0) {
            speedProgress.setProgress((float)player.getMotion()/(float)player.getInitialMotion());
        } else {
            speedProgress.setProgress(0);
        }
    }

}
