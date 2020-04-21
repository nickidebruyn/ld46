/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.screens;

import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.games.platform2d.editor.Platform2DEditScreen;
import com.jme3.math.Vector3f;
import za.co.bruynhuis.findlight.MainApplication;
import za.co.bruynhuis.findlight.game.Game;

/**
 *
 * @author NideBruyn
 */
public class EditScreen extends Platform2DEditScreen {

    public static final String NAME = "edit";

    @Override
    protected void doTestAction() {
        PlayScreen playScreen = (PlayScreen) baseApplication.getScreenManager().getScreen(PlayScreen.NAME);
        playScreen.setTest(true);
        playScreen.setLevelName(fileName);
        ((MainApplication) baseApplication).resetGame();

        showScreen(PlayScreen.NAME);

    }

    @Override
    protected Platform2DGame initGame() {
        prefix = "ld46-";
        rows = 14;
        columns = 24;

        return new Game((MainApplication) baseApplication, rootNode);

    }

    @Override
    protected void initUI() {

//        toolbar.addToolTopButton("link", null, 0);
//        toolbar.setDoubleSideBar(false);
        toolbar.addToolButton("terrain-corner-1", "Textures/tileset1.png", 0, 4, 4, 0, 0);
        toolbar.addToolButton("terrain-corner-2", "Textures/tileset1.png", 0, 4, 4, 1, 3);//
        toolbar.addToolButton("terrain-wall-1", "Textures/tileset1.png", 0, 4, 4, 1, 0);
        toolbar.addToolButton("terrain-wall-2", "Textures/tileset1.png", 0, 4, 4, 0, 2);
        toolbar.addToolButton("terrain-door-1", "Textures/tileset1.png", 0, 4, 4, 0, 1);
        toolbar.addToolButton("terrain-door-entry", "Textures/tileset1.png", 0, 4, 4, 1, 1);
        toolbar.addToolButton("terrain-door-3", "Textures/tileset1.png", 0, 4, 4, 2, 1);
        toolbar.addToolButton("terrain-rock-1", "Textures/tileset1.png", 0, 4, 4, 3, 3);//
        toolbar.addToolButton("floor-1", "Textures/tileset1.png", 0, 4, 4, 2, 0);
        toolbar.addToolButton("edge-corner-1", "Textures/tileset1.png", 0, 4, 4, 2, 2);
        toolbar.addToolButton("edge-wall-1", "Textures/tileset1.png", 0, 4, 4, 1, 2);
        toolbar.addToolButton("edge-side-1", "Textures/tileset1.png", 0, 4, 4, 3, 2);
        toolbar.addToolButton("edge-inside-1", "Textures/tileset1.png", 0, 4, 4, 2, 3);//
        toolbar.addToolButton("start-1", "Textures/tileset1.png", 0, 4, 4, 3, 0);
//        toolbar.addToolButton("terrain-door-left", "Textures/tileset1.png", 0, 4, 4, 1, 1);
//        toolbar.addToolButton("terrain-door-right", "Textures/tileset1.png", 0, 4, 4, 1, 1);
//        toolbar.addToolButton("terrain-door-up", "Textures/tileset1.png", 0, 4, 4, 1, 1);
//        toolbar.addToolButton("terrain-door-down", "Textures/tileset1.png", 0, 4, 4, 1, 1);

        toolbar.addToolButton("enemy-1", "Textures/enemies.png", 0, 5, 5, 0, 0);
        toolbar.addToolButton("enemy-2", "Textures/enemies.png", 0, 5, 5, 0, 1);
        toolbar.addToolButton("enemy-3", "Textures/enemies.png", 0, 5, 5, 0, 2);
        toolbar.addToolButton("terrain-trap-1", "Textures/tileset1.png", 0, 4, 4, 3, 1);
        toolbar.addToolButton("chest-fire", "Textures/pickups.png", 0, 5, 5, 0, 4);
        toolbar.addToolButton("chest-motion", "Textures/pickups.png", 0, 5, 5, 0, 4);
        toolbar.addToolButton("chest-none", "Textures/pickups.png", 0, 5, 5, 0, 4);

    }

    @Override
    protected void initCamera() {
        baseApplication.getViewPort().setBackgroundColor(MainApplication.BACKGROUND_COLOR);
        cameraFrustrum = MainApplication.CAMERA_HEIGHT;
        ((MainApplication) baseApplication).setCameraDistanceFrustrum(MainApplication.CAMERA_HEIGHT);
        camera.setLocation(new Vector3f(11f, 7, MainApplication.CAMERA_HEIGHT));
    }

    @Override
    protected void show() {
        super.show(); //To change body of generated methods, choose Tools | Templates.
        setPreviousScreen(MenuScreen.NAME);
    }

}
