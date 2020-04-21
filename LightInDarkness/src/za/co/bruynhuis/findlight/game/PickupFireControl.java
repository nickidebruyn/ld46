/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.game;

import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.Animation;
import com.bruynhuis.galago.sprite.AnimationListener;
import com.bruynhuis.galago.util.Timer;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class PickupFireControl extends AbstractControl implements AnimationListener {

    private Game game;
    private AnimatedSprite sprite;
    private int amount;
    private boolean show;
    private boolean pickedUp = false;
    private Timer pickupTimer = new Timer(1000);

    public PickupFireControl(Game game, AnimatedSprite sprite, int amount) {
        this.game = game;
        this.amount = amount;
        this.sprite = sprite;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (!show) {
            sprite.addAnimationListener(this);
            sprite.play("show", true, false, false);
            show = true;
            pickupTimer.start();

        }

        if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

            pickupTimer.update(tpf);
            if (pickupTimer.finished()) {
                spatial.setCullHint(Spatial.CullHint.Always);
                pickedUp = true;

                sprite.play("done", true, false, false);
                pickupTimer.stop();
            }

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    @Override
    public void animationStart(Animation animation) {

    }

    @Override
    public void animationDone(Animation animation) {

        if (animation.getName().equals("show")) {
            sprite.play("idle", true, false, true);
        }

    }

    public void doPickup() {
        if (!pickedUp) {
            spatial.setCullHint(Spatial.CullHint.Always);
            pickedUp = true;
            ((Player) game.getPlayer()).addHealth();
            sprite.play("done", true, false, false);
            game.getBaseApplication().getSoundManager().playSound("crystal");
        }

    }
}
