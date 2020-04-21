/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Elastic;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
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
public class WinScreen extends AbstractScreen {

    public static final String NAME = "Win";
    private Button backButton;
    private Button retryButton;
    private Label info;
    private MainApplication mainApplication;
    private Sprite backgroundSprite;
    private float scale = 0.0255f;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

//        background = new Image(hudPanel, "Interface/background.png", 1280, 800);
//        title = new Label(hudPanel, "Flame Bearer", 58, 600, 30);
//        title.centerTop(0, 0);
        info = new Label(hudPanel, "Thank you for playing my game.\nThis is my entry to Ludum Dare 46\nwith the theme (Keep it alive).", 28, 600, 400);
        info.centerAt(-200, -50);
        info.setAlignment(TextAlign.LEFT);
        info.setVerticalAlignment(TextAlign.TOP);

        retryButton = new Button(hudPanel, "play-again-button", "Play Again");
        retryButton.centerAt(500, 0);
        retryButton.addTouchButtonListener(new TouchButtonAdapter() {
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
        retryButton.moveFromToCenter(500, 0, 510, 0, 1, 0.5f, Elastic.IN, Tween.INFINITY, true);

        backButton = new Button(hudPanel, "leave-button", "Leave");
        backButton.centerAt(500, -50);
        backButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    mainApplication.getSoundManager().playSound("button");
                    showScreen(MenuScreen.NAME);

                }
            }

        });

    }

    @Override
    protected void load() {

        backgroundSprite = SpriteUtils.addSprite(rootNode, "Interface/win.png", 1280 * scale, 800 * scale, 0, 0, -20);

        Spatial particles = assetManager.loadModel("Models/menu-dust.j3o");
        particles.move(1, 5f, 0);
        rootNode.attachChild(particles);

        camera.setLocation(new Vector3f(0, 0, 10));
        mainApplication.setCameraDistanceFrustrum(10);

        info.setTransparency(0);
    }

    @Override
    protected void show() {
        setPreviousScreen(MenuScreen.NAME);
        
        info.setTransparency(0);

        info.fadeFromTo(0, 1, 3, 0, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                info.setTransparency(1);
            }
        });

    }

    @Override
    protected void exit() {
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }

}
