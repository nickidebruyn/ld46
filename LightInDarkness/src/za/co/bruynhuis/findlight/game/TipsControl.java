/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.game;

import com.bruynhuis.galago.util.Timer;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class TipsControl extends AbstractControl {

    private Game game;
    private BitmapText bitmapText;
    private Timer timer = new Timer(200);
    private boolean fadeActive = false;
    private float alpha = 1f;

    public TipsControl(Game game, BitmapText bitmapText) {
        this.game = game;
        this.bitmapText = bitmapText;
        timer.start();
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {

            timer.update(tpf);
            if (timer.finished()) {
                fadeActive = true;
                alpha = 1f;
                timer.stop();

            }

            if (fadeActive) {
                alpha -= tpf * 0.2f;
                System.out.println("Tip alpha: " + alpha);
                if (alpha <= 0) {
                    alpha = 0;
                    bitmapText.setAlpha(alpha);
                    bitmapText.setColor(new ColorRGBA(0.12f, 0.1f, 0.1f, alpha));
                    fadeActive = false;
                }

            }
        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

}
