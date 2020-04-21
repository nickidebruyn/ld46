/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import za.co.bruynhuis.findlight.screens.CreditsScreen;
import za.co.bruynhuis.findlight.screens.EditScreen;
import za.co.bruynhuis.findlight.screens.GameoverScreen;
import za.co.bruynhuis.findlight.screens.IntroScreen;
import za.co.bruynhuis.findlight.screens.MenuScreen;
import za.co.bruynhuis.findlight.screens.PlayScreen;
import za.co.bruynhuis.findlight.screens.SettingsScreen;
import za.co.bruynhuis.findlight.screens.WinScreen;

/**
 * This game is a TOP Down platformer. It plays out in a dungeon filled with
 * enemies. You are the carrier of a small little flame which also lights your
 * way in the dark. As time passes this flame burns out. The game consist of
 * rooms with 0 to 3 doors. Each door leads to another room. Final room has a
 * BOSS which has to be defeated.
 *
 * @author NideBruyn
 */
public class MainApplication extends Base2DApplication {

    public static final float CAMERA_HEIGHT = 9f;
    private boolean devMode = false;

    public static void main(String[] args) {
        new MainApplication();
    }

    public MainApplication() {
        super("Light in Darkness", 1280, 800, "flame-bearer.save", "Interface/Fonts/GillSansUltraBoldCondensed.fnt", null, true);
    }

    @Override
    protected void preInitApp() {
//        BACKGROUND_COLOR = ColorUtils.rgb(24, 20, 18);
    }

    @Override
    protected void postInitApp() {
        showScreen(MenuScreen.NAME);

    }

    public String getLastLevel() {
        return getGameSaves().getGameData().getProperties().getProperty("level");
    }

    public void resetGame() {
        //Setup the default level
        getGameSaves().getGameData().getProperties().remove("health");
        getGameSaves().getGameData().getProperties().remove("speed");
        getGameSaves().getGameData().getProperties().remove("target");
        getGameSaves().getGameData().getProperties().remove("level");

        Set set = getGameSaves().getGameData().getProperties().keySet();
        List<String> removeList = new ArrayList<>();

        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            if (key != null && (key.startsWith("enemy-") || key.startsWith("chest-"))) {
                log("Remove " + key + " on testing");
                removeList.add(key);
            }

        }

        for (int i = 0; i < removeList.size(); i++) {
            String key = removeList.get(i);
            getGameSaves().getGameData().getProperties().remove(key);
        }

        getGameSaves().getGameData().getProperties().setProperty("level", "default");
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen(MenuScreen.NAME, new MenuScreen());
        screenManager.loadScreen(EditScreen.NAME, new EditScreen());
        screenManager.loadScreen(PlayScreen.NAME, new PlayScreen());
        screenManager.loadScreen(GameoverScreen.NAME, new GameoverScreen());
        screenManager.loadScreen(WinScreen.NAME, new WinScreen());
        screenManager.loadScreen(IntroScreen.NAME, new IntroScreen());
        screenManager.loadScreen(CreditsScreen.NAME, new CreditsScreen());
        screenManager.loadScreen(SettingsScreen.NAME, new SettingsScreen());
    }

    @Override
    public void initModelManager(ModelManager modelManager) {
    }

    @Override
    protected void initSound(SoundManager soundManager) {
        soundManager.loadSoundFx("button", "Sounds/button.wav");
        soundManager.loadSoundFx("gameover", "Sounds/gameover.wav");
        soundManager.loadSoundFx("win", "Sounds/win.wav");
        soundManager.loadSoundFx("fireball", "Sounds/shoot.wav");
        soundManager.loadSoundFx("arrow", "Sounds/arrow.wav");
        soundManager.loadSoundFx("arrow-hit", "Sounds/arrow-hit.wav");
        soundManager.loadSoundFx("fireball-enemy", "Sounds/fireball-enemy.wav");
        soundManager.loadSoundFx("bullet-hit", "Sounds/bullet-hit.wav");
        soundManager.loadSoundFx("crystal", "Sounds/crystal.wav");
        soundManager.loadSoundFx("enemy-hit", "Sounds/enemy-hit.wav");
        soundManager.loadSoundFx("player-hit", "Sounds/player-hit.wav");
        soundManager.loadSoundFx("chest-hit", "Sounds/chest-hit.wav");

        soundManager.loadMusic("spider", "Sounds/spider.wav");
        soundManager.setMusicVolume("spider", 0.5f);

        soundManager.loadMusic("giant-walk", "Sounds/giant-walk.wav");
        soundManager.setMusicVolume("giant-walk", 0.5f);

        soundManager.loadMusic("bee", "Sounds/bee.wav");
        soundManager.setMusicVolume("bee", 0.5f);

        soundManager.loadMusic("walk", "Sounds/walk.wav");
        soundManager.setMusicVolume("walk", 0.5f);
        soundManager.setMusicSpeed("walk", 1.2f);

        soundManager.loadMusic("run", "Sounds/run.wav");
        soundManager.setMusicVolume("run", 0.5f);
        soundManager.setMusicSpeed("run", 0.8f);
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
        effectManager.loadEffect("explode", "Models/explode.j3o");
        effectManager.loadEffect("explode-green", "Models/explode-green.j3o");
        effectManager.loadEffect("explode-red", "Models/explode-red.j3o");
        effectManager.loadEffect("explode-arrow", "Models/explode-arrow.j3o");
        effectManager.loadEffect("explode-bee", "Models/explode-bee.j3o");
        effectManager.loadEffect("explode-chest", "Models/explode-chest.j3o");
    }

    @Override
    protected void initTextures(TextureManager textureManager) {
//        textureManager.setPixelated(true);
    }

    @Override
    protected void initFonts(FontManager fontManager) {
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

}
