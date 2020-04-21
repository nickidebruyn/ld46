/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.game;

import com.bruynhuis.galago.control.camera.CameraShaker;
import com.bruynhuis.galago.games.platform2d.Tile;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class ChestControl extends AbstractControl {

    public static final int CHEST_NONE = 0;
    public static final int CHEST_FIRE = 1;
    public static final int CHEST_MOTION = 2;

    private Game game;
    private RigidBodyControl rigidBodyControl;
    private int type = 0;
    private boolean destroy;
    private int health = 2;

    public ChestControl(Game game, RigidBodyControl rigidBodyControl, int type) {
        this.game = game;
        this.rigidBodyControl = rigidBodyControl;
        this.type = type;

    }

    @Override
    protected void controlUpdate(float tpf) {

        if (destroy) {

            Tile tile = game.getTile((Sprite) spatial);
            if (tile != null) {
                String uuid = tile.getProperties().getProperty("uuid");
                game.getBaseApplication().getGameSaves().getGameData().getProperties().setProperty("chest-" + uuid, tile.getProperties().getProperty("uuid"));

            }

            //Make an explosion
            game.getBaseApplication().getEffectManager().doEffect("explode-chest", rigidBodyControl.getPhysicLocation().clone());
//            game.getBaseApplication().getSoundManager().playSound(explodeSound);

//            ((Game) player.getGame()).getChromaticAberration().doEffect(0.1f, new Vector3f(0.015f, 0.0f, -0.015f));
//            Vector3f pos = player.getGame().getBaseApplication().getCamera().getScreenCoordinates(rigidBodyControl.getPhysicLocation());
//            ((Game) player.getGame()).getShockwaveFilter().doEffect(2.2f, new Vector2f(pos.x, pos.y));
            if (type == CHEST_FIRE) {
                game.addFireCrystal(rigidBodyControl.getPhysicLocation().clone());

            } else if (type == CHEST_MOTION) {
                game.addSpeedCrystal(rigidBodyControl.getPhysicLocation().clone());

            }

            game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rigidBodyControl);
            spatial.removeFromParent();

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public void doHit() {

        if (!destroy) {
            game.getCameraShaker().shake(CameraShaker.LARGE_AMOUNT, 20);
            game.getBaseApplication().getSoundManager().playSound("chest-hit");

            health = health - 1;
            if (health <= 0) {
                destroy = true;

            }
        }
    }

}
