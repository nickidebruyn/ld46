/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.SpriteUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import za.co.bruynhuis.findlight.MainApplication;
import za.co.bruynhuis.findlight.ui.Button;

/**
 *
 * @author NideBruyn
 */
public class IntroScreen extends AbstractScreen {

    public static final String NAME = "Intro";
    private Button playButton;
    private MainApplication mainApplication;
    private Sprite backgroundSprite;
    private float scale = 0.0255f;
    private boolean exited;
    private Image overlay;
    private Label info;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

//        background = new Image(hudPanel, "Interface/background.png", 1280, 800);
        info = new Label(hudPanel, "", 28, 600, 400);
        info.centerAt(-200, 100);
        info.setAlignment(TextAlign.LEFT);
        info.setVerticalAlignment(TextAlign.TOP);

        playButton = new Button(hudPanel, "playButton-button", "Continue");
        playButton.rightBottom(-25, 5);
        playButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    exited = true;
                    mainApplication.getSoundManager().playSound("button");
                    showScreen(PlayScreen.NAME);

                }
            }

        });

        overlay = new Image(hudPanel, "Resources/fade.png", 1280, 800);
        overlay.setBackgroundColor(ColorRGBA.Black);
        overlay.center();

    }

    @Override
    protected void load() {
        
        exited = false;

        overlay.setBackgroundColor(ColorRGBA.Black);

        backgroundSprite = SpriteUtils.addSprite(rootNode, "Interface/intro.png", 1280 * scale, 800 * scale, 0, 0, -20);

        Spatial particles = assetManager.loadModel("Models/menu-dust.j3o");
        particles.move(4, 5f, 0);
        rootNode.attachChild(particles);

        camera.setLocation(new Vector3f(0, 0, 10));
        mainApplication.setCameraDistanceFrustrum(10);

    }

    @Override
    protected void show() {
        setPreviousScreen(MenuScreen.NAME);

        playButton.setVisible(false);
        showTake1();

    }

    private void showTake1() {
        if (exited) {
            return;
        }
        info.setVisible(false);
        backgroundSprite.setImage("Interface/intro.png");
        overlay.fadeFromTo(1, 0, 3, 0, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                showTake2();
            }
        });

    }

    private void showTake2() {
        if (exited) {
            return;
        }
        info.setText("The story of a boy\nwho fell into\nthe deep.");
        info.setVisible(true);
        info.fadeFromTo(0, 1, 3, 0, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                showTake3();
            }
        });

    }

    private void showTake3() {
        if (exited) {
            return;
        }

        overlay.fadeFromTo(0, 1, 3, 2, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                playButton.setVisible(true);
                showTake4();
            }
        });

    }

    private void showTake4() {
        if (exited) {
            return;
        }
        info.setVisible(false);
        backgroundSprite.setImage("Interface/intro2.png");
        overlay.fadeFromTo(1, 0, 3, 0, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                showTake5();
            }
        });

    }

    private void showTake5() {
        if (exited) {
            return;
        }
        info.setText("As darkness awake\nhe has but one light\nto keep alive.");
        info.setVisible(true);
        info.fadeFromTo(0, 1, 3, 0, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                showTake6();

            }
        });

    }

    private void showTake6() {
        if (exited) {
            return;
        }

        overlay.fadeFromTo(0, 1, 3, 2, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                playButton.setVisible(false);
                showScreen(PlayScreen.NAME);
            }
        });

    }

    @Override
    protected void exit() {
        exited = true;
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }

}
