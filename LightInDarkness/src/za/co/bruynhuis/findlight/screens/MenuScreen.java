/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Elastic;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.SpriteUtils;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import za.co.bruynhuis.findlight.MainApplication;
import za.co.bruynhuis.findlight.ui.Button;

/**
 *
 * @author NideBruyn
 */
public class MenuScreen extends AbstractScreen {

    public static final String NAME = "MenuScreen";
    private Image background;
    private Button playButton;
    private Button continueButton;
    private Button settingsButton;
    private Button helpButton;
    private Button editButton;
    private Button exitButton;
    private MainApplication mainApplication;
    private Sprite backgroundSprite;
    private float scale = 0.0255f;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

//        background = new Image(hudPanel, "Interface/background.png", 1280, 800);
//        title = new Label(hudPanel, "Flame Bearer", 58, 600, 30);
//        title.centerTop(0, 0);
        continueButton = new Button(hudPanel, "contine-button", "Continue Game");
        continueButton.centerAt(450, 0);
        continueButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    mainApplication.getSoundManager().playSound("button");
                    PlayScreen playScreen = (PlayScreen) baseApplication.getScreenManager().getScreen(PlayScreen.NAME);
                    playScreen.setTest(false);
                    String targetLevel = mainApplication.getLastLevel();
                    playScreen.setLevelName("ld46-" + targetLevel + EditScreen.FILE_EXT);
                    showScreen(PlayScreen.NAME);

                }
            }

        });
        continueButton.moveFromToCenter(450, 0, 460, 0, 1, 0.5f, Elastic.IN, Tween.INFINITY, true);

        playButton = new Button(hudPanel, "play-button", "New Game");
        playButton.centerAt(500, -50);
        playButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    mainApplication.getSoundManager().playSound("button");
                    mainApplication.resetGame();
                    PlayScreen playScreen = (PlayScreen) baseApplication.getScreenManager().getScreen(PlayScreen.NAME);
                    playScreen.setTest(false);
                    String targetLevel = mainApplication.getLastLevel();
                    playScreen.setLevelName("ld46-" + targetLevel + EditScreen.FILE_EXT);
                    showScreen(IntroScreen.NAME);

                }
            }

        });

//        settingsButton = new Button(hudPanel, "settings-button", "Settings");
//        settingsButton.centerAt(550, -100);
//        settingsButton.addTouchButtonListener(new TouchButtonAdapter() {
//            @Override
//            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
//                if (isActive()) {
//                    mainApplication.getSoundManager().playSound("button");
//
//                }
//            }
//
//        });
//
//        helpButton = new Button(hudPanel, "help-button", "Help");
//        helpButton.centerAt(600, -150);
//        helpButton.addTouchButtonListener(new TouchButtonAdapter() {
//            @Override
//            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
//                if (isActive()) {
//                    mainApplication.getSoundManager().playSound("button");
//
//                }
//            }
//
//        });

        if (mainApplication.isDevMode()) {
            editButton = new Button(hudPanel, "edit-button", "Edit");
            editButton.centerAt(600, -200);
            editButton.addTouchButtonListener(new TouchButtonAdapter() {
                @Override
                public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                    if (isActive()) {
                        mainApplication.getSoundManager().playSound("button");
                        showScreen(EditScreen.NAME);
                    }
                }

            });

        } else {
            exitButton = new Button(hudPanel, "exit-button", "Exit");
            exitButton.centerAt(550, -100);
            exitButton.addTouchButtonListener(new TouchButtonAdapter() {
                @Override
                public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                    if (isActive()) {
                        mainApplication.getSoundManager().playSound("button");
                        exitScreen();
                    }
                }

            });
        }

    }

    @Override
    protected void load() {

        backgroundSprite = SpriteUtils.addSprite(rootNode, "Interface/background.png", 1280 * scale, 800 * scale, 0, 0, -20);

        Spatial particles = assetManager.loadModel("Models/menu-dust.j3o");
        particles.move(1, 5f, 0);
        rootNode.attachChild(particles);

        camera.setLocation(new Vector3f(0, 0, 10));
        mainApplication.setCameraDistanceFrustrum(10);

    }

    @Override
    protected void show() {
        setPreviousScreen(null);
        String level = mainApplication.getLastLevel();
        if (level == null) {
            continueButton.hide();
        } else {
            continueButton.show();
        }

    }

    @Override
    protected void exit() {
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }

}
