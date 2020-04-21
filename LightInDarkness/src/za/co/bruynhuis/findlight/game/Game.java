/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.game;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.control.camera.CameraShaker;
import com.bruynhuis.galago.filters.ChromaticAberrationFilter;
import com.bruynhuis.galago.filters.ShockwaveFilter;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import static com.bruynhuis.galago.games.platform2d.Platform2DGame.SHAPE;
import com.bruynhuis.galago.games.platform2d.Tile;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.Animation;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.ui.FontStyle;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import java.util.ArrayList;

/**
 *
 * @author NideBruyn
 */
public class Game extends Platform2DGame {

    private Material tilesetMaterial;
    private Material enemiesMaterial;
    private Material pickupsMaterial;
    private RigidBodyControl terrainRigidBodyControl;
    private CameraShaker cameraShaker;
    private FilterPostProcessor fpp;
    private ChromaticAberrationFilter chromaticAberration;
    private ShockwaveFilter shockwaveFilter;

    public Game(Base2DApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);

//        tilesetMaterial = baseApplication.getAssetManager().loadMaterial("Materials/tileset1.j3m");
    }

    @Override
    public void init() {

        if (!isEdit()) {
            cameraShaker = new CameraShaker(baseApplication.getCamera(), rootNode);

            fpp = new FilterPostProcessor(baseApplication.getAssetManager());
            baseApplication.getViewPort().addProcessor(fpp);

            chromaticAberration = new ChromaticAberrationFilter();
            fpp.addFilter(chromaticAberration);

            shockwaveFilter = new ShockwaveFilter();
            shockwaveFilter.setShockParams(new Vector3f(5, 0.2f, 0.2f));
            fpp.addFilter(shockwaveFilter);

        }

        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {

                if (spatial instanceof Sprite) {
//                    log("spatial.getName() = " + spatial.getName());
                    if (spatial.getName().startsWith("enemy") && !isEdit()) {
                        Tile tile = getTile((Sprite) spatial);
                        if (tile != null) {
                            String uuid = tile.getProperties().getProperty("uuid");
                            if (baseApplication.getGameSaves().getGameData().getProperties().getProperty("enemy-" + uuid) != null) {
                                log("Enemy destroyed: " + uuid);
                                removeTile((Sprite) spatial);
                            }
                        }

                    } else if (spatial.getName().startsWith("obstacle") && !isEdit()) {
                        Tile tile = getTile((Sprite) spatial);
                        if (tile != null) {
                            String uuid = tile.getProperties().getProperty("uuid");
                            if (baseApplication.getGameSaves().getGameData().getProperties().getProperty("chest-" + uuid) != null) {
                                log("Chest destroyed: " + uuid);
                                removeTile((Sprite) spatial);
                            }
                        }

                    } else if (spatial.getName().startsWith("vegetation")) {
                        Tile tile = getTile((Sprite) spatial);
                        if (tile != null) {
                            String tip = tile.getProperties().getProperty("tip");
                            if (tip != null) {
                                log("Adding tip to screen " + tip);
                                Sprite s = (Sprite) spatial;

                                //Add text
                                BitmapFont bitmapFont = baseApplication.getFontManager().getBitmapFonts(new FontStyle(FontManager.DEFAULT_FONT, 18));
                                BitmapText text = bitmapFont.createLabel(tip);
                                text.setBox(new Rectangle(-2, 0.5f, 5, 0f));
                                text.setAlignment(BitmapFont.Align.Center);
//                                text.setText(tip);
                                text.setSize(0.6f);
                                text.setColor(new ColorRGBA(0.12f, 0.1f, 0.11f, 0.5f));
                                text.setLocalTranslation(0f, 0.0f, 0.0f);
//                                text.setQueueBucket(RenderQueue.Bucket.Transparent);
//                                text.setShadowMode(RenderQueue.ShadowMode.Off);

                                BillboardControl billboardControl = new BillboardControl();
                                billboardControl.setAlignment(BillboardControl.Alignment.Screen);
                                text.addControl(billboardControl);
                                s.attachChild(text);

//                                text.addControl(new TipsControl(Game.this, text));
                            }

                        }

                    }
                }

            }
        };
        levelNode.depthFirstTraversal(sgv);

    }

    public CameraShaker getCameraShaker() {
        return cameraShaker;
    }

    public ShockwaveFilter getShockwaveFilter() {
        return shockwaveFilter;
    }

    public ChromaticAberrationFilter getChromaticAberration() {
        return chromaticAberration;
    }

    @Override
    protected void initTerrainList(ArrayList<String> list) {
        list.add("terrain-corner-1");
        list.add("terrain-corner-2");
        list.add("terrain-wall-1");
        list.add("terrain-wall-2");
        list.add("terrain-door-1");
//        list.add("terrain-door-entry");
        list.add("terrain-door-3");
        list.add("terrain-trap-1");
        list.add("terrain-rock-1");

    }

    @Override
    protected void initEnemyList(ArrayList<String> list) {

        list.add("enemy-1");
        list.add("enemy-2");
        list.add("enemy-3");

    }

    @Override
    protected void initObstacleList(ArrayList<String> list) {

        list.add("chest-fire");
        list.add("chest-motion");
        list.add("chest-none");

    }

    @Override
    protected void initStaticList(ArrayList<String> list) {

    }

    @Override
    protected void initPickupList(ArrayList<String> list) {
        list.add("terrain-door-entry");
//        list.add("terrain-door-right");
//        list.add("terrain-door-up");
//        list.add("terrain-door-down");

    }

    @Override
    protected void initVegetationList(ArrayList<String> list) {
        list.add("floor-1");
        list.add("edge-corner-1");
        list.add("edge-side-1");
        list.add("edge-inside-1");
        list.add("edge-wall-1");

    }

    @Override
    protected void initSkyList(ArrayList<String> list) {

    }

    @Override
    protected void initFrontLayer1List(ArrayList<String> list) {

    }

    @Override
    protected void initFrontLayer2List(ArrayList<String> list) {

    }

    @Override
    protected void initBackLayer1List(ArrayList<String> list) {

    }

    @Override
    protected void initBackLayer2List(ArrayList<String> list) {

    }

    @Override
    protected void initStartList(ArrayList<String> list) {
        list.add("start-1");

    }

    @Override
    protected void initEndList(ArrayList<String> list) {

    }

    private void addTerrainCollisionShape(CollisionShape collisionShape, Sprite sprite) {
        sprite.setUserData(SHAPE, collisionShape);
        sprite.setMaterial(tilesetMaterial);
        sprite.move(0, 0, 0);

        if (terrainRigidBodyControl == null) {
            terrainRigidBodyControl = new RigidBodyControl(collisionShape, 0);
            terrainRigidBodyControl.setAutoSleepingEnabled(true);
            terrainRigidBodyControl.setRestitution(0);
            terrainRigidBodyControl.setFriction(1f);
            terrainRigidBodyControl.setDensity(10);
            sprite.addControl(terrainRigidBodyControl);
        } else {
            terrainRigidBodyControl.addCollisionShape(collisionShape);
        }
    }

    @Override
    public Sprite getItem(String item) {

        Sprite sprite = null;

        if (isEdit()) {
            tilesetMaterial = baseApplication.getAssetManager().loadMaterial("Materials/tileset1-unshaded.j3m");
            enemiesMaterial = baseApplication.getAssetManager().loadMaterial("Materials/enemies-unshaded.j3m");
            pickupsMaterial = baseApplication.getAssetManager().loadMaterial("Materials/pickups-unshaded.j3m");

        } else {
            tilesetMaterial = baseApplication.getAssetManager().loadMaterial("Materials/tileset1.j3m");
            enemiesMaterial = baseApplication.getAssetManager().loadMaterial("Materials/enemies.j3m");
            pickupsMaterial = baseApplication.getAssetManager().loadMaterial("Materials/pickups.j3m");

//            MatParamTexture mpt = tilesetMaterial.getTextureParam("DiffuseMap");
//            mpt.getTextureValue().setMinFilter(Texture.MinFilter.NearestNoMipMaps);
//            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
//
//            mpt = enemiesMaterial.getTextureParam("DiffuseMap");
//            mpt.getTextureValue().setMinFilter(Texture.MinFilter.NearestNoMipMaps);
//            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }

        if (item.startsWith("floor")) {
            sprite = new Sprite("floor", 1, 1, 4, 4, 2, 0);
            sprite.setMaterial(tilesetMaterial);

        } else if (item.startsWith("edge-corner-1")) {
            sprite = new Sprite("edge", 1, 1, 4, 4, 2, 2);
            sprite.setMaterial(tilesetMaterial);

        } else if (item.startsWith("edge-wall-1")) {
            sprite = new Sprite("edge", 1, 1, 4, 4, 1, 2);
            sprite.setMaterial(tilesetMaterial);

        } else if (item.startsWith("edge-side-1")) {
            sprite = new Sprite("edge", 1, 1, 4, 4, 3, 2);
            sprite.setMaterial(tilesetMaterial);

        } else if (item.startsWith("edge-inside-1")) {
            sprite = new Sprite("edge", 1, 1, 4, 4, 2, 3);
            sprite.setMaterial(tilesetMaterial);

        } else if (item.startsWith("terrain-door-entry")) {
            sprite = new Sprite("door", 1, 1, 4, 4, 1, 1);
            sprite.setMaterial(tilesetMaterial);
            sprite.setUserData("door", "door");

            RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight() * 0.15f), 0);
            sprite.addControl(rbc);

//        } else if (item.startsWith("terrain-door-right")) {
//            sprite = new Sprite("door", 1, 1, 4, 4, 1, 1);
//            sprite.setMaterial(tilesetMaterial);
//
//        } else if (item.startsWith("terrain-door-up")) {
//            sprite = new Sprite("door", 1, 1, 4, 4, 1, 1);
//            sprite.setMaterial(tilesetMaterial);
//
//        } else if (item.startsWith("terrain-door-down")) {
//            sprite = new Sprite("door", 1, 1, 4, 4, 1, 1);
//            sprite.setMaterial(tilesetMaterial);
        } else if (item.startsWith("chest-fire")) {
            sprite = new Sprite("chest", 1.5f, 1.5f, 5, 5, 0, 4);
            sprite.setMaterial(pickupsMaterial);
            sprite.flipCoords(true);
            sprite.flipHorizontal(false);

            RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(0.6f, 0.6f), 0);
            sprite.addControl(rbc);

            sprite.addControl(new ChestControl(this, rbc, ChestControl.CHEST_FIRE));

        } else if (item.startsWith("chest-motion")) {
            sprite = new Sprite("chest", 1.5f, 1.5f, 5, 5, 0, 4);
            sprite.setMaterial(pickupsMaterial);
            sprite.flipCoords(true);
            sprite.flipHorizontal(false);

            RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(0.6f, 0.6f), 0);
            sprite.addControl(rbc);

            sprite.addControl(new ChestControl(this, rbc, ChestControl.CHEST_MOTION));

        } else if (item.startsWith("chest-none")) {
            sprite = new Sprite("chest", 1.5f, 1.5f, 5, 5, 0, 4);
            sprite.setMaterial(pickupsMaterial);
            sprite.flipCoords(true);
            sprite.flipHorizontal(false);

            RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(0.6f, 0.6f), 0);
            sprite.addControl(rbc);

            sprite.addControl(new ChestControl(this, rbc, ChestControl.CHEST_NONE));

        } else if (item.startsWith("terrain")) {

            CollisionShape collisionShape = null;

            if (item.endsWith("corner-1")) {
                sprite = new Sprite("wall", 1, 1, 4, 4, 0, 0);
                collisionShape = new BoxCollisionShape(sprite.getWidth(), sprite.getHeight());

            } else if (item.endsWith("corner-2")) {
                sprite = new Sprite("wall", 1, 1, 4, 4, 1, 3);
                collisionShape = new BoxCollisionShape(sprite.getWidth(), sprite.getHeight());

            } else if (item.endsWith("wall-1")) {
                sprite = new Sprite("wall", 1, 1, 4, 4, 1, 0);
                collisionShape = new BoxCollisionShape(sprite.getWidth(), sprite.getHeight() * 0.75f);

            } else if (item.endsWith("wall-2")) {
                sprite = new Sprite("wall", 1, 1, 4, 4, 0, 2);
                collisionShape = new BoxCollisionShape(sprite.getWidth(), sprite.getHeight() * 0.75f);

            } else if (item.endsWith("door-1")) {
                sprite = new Sprite("door", 1, 1, 4, 4, 0, 1);
                collisionShape = new BoxCollisionShape(sprite.getWidth(), sprite.getHeight() * 0.25f);

//            } else if (item.endsWith("door-entry")) {
//                sprite = new Sprite("door", 1, 1, 4, 4, 1, 1);
//                collisionShape = new BoxCollisionShape(sprite.getWidth(), sprite.getHeight() * 0.25f);
            } else if (item.endsWith("door-3")) {
                sprite = new Sprite("door", 1, 1, 4, 4, 2, 1);
                collisionShape = new BoxCollisionShape(sprite.getWidth(), sprite.getHeight() * 0.25f);

            } else if (item.endsWith("trap-1")) {
                sprite = new Sprite("door", 1, 1, 4, 4, 3, 1);
                collisionShape = new BoxCollisionShape(sprite.getWidth(), sprite.getHeight() * 0.25f);
                sprite.addControl(new ShootingWallControl(this));

            } else if (item.endsWith("rock-1")) {
                sprite = new Sprite("door", 1, 1, 4, 4, 3, 3);
                collisionShape = new BoxCollisionShape(sprite.getWidth(), sprite.getHeight() * 0.25f);

            }

            //add the terrain stuff
            if (sprite != null && collisionShape != null) {
                addTerrainCollisionShape(collisionShape, sprite);
                sprite.setQueueBucket(RenderQueue.Bucket.Transparent);

                return sprite;
            }

        } else if (item.equals("start-1")) {
            sprite = new Sprite("start", 1, 1, 4, 4, 3, 0);
            sprite.setMaterial(tilesetMaterial);

        } else if (item.equals("enemy-1")) {
            sprite = new AnimatedSprite("enemy", 1, 1, 5, 5, 1);
            sprite.setMaterial(enemiesMaterial);
            sprite.showIndex(0);
            sprite.flipCoords(true);
            sprite.flipHorizontal(false);
            sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
            sprite.move(0, 0, 0.02f);

            ((AnimatedSprite) sprite).addAnimation(new Animation("idle", 0, 1, 15));
            ((AnimatedSprite) sprite).addAnimation(new Animation("walk", 0, 3, 8));

            Sprite shadow = new Sprite("shadow", 0.8f, 0.8f);
            shadow.setMaterial(getBaseApplication().getAssetManager().loadMaterial("Materials/shadow.j3m"));
            shadow.setQueueBucket(RenderQueue.Bucket.Transparent);
            sprite.attachChild(shadow);
            shadow.move(0, 0.1f, 0);

            RigidBodyControl rbc = new RigidBodyControl(new CircleCollisionShape(0.3f), 1);
            rbc.setGravityScale(0);
            rbc.setLinearDamping(3);
            sprite.addControl(rbc);

            EnemyControl enemyControl = new EnemyControl(this, rbc);
            enemyControl.setWalkSound("spider");
            enemyControl.setDamageKickback(8);
            enemyControl.setDetectDistance(4);
            enemyControl.setWalkSpeed(2);
            enemyControl.setHealth(2);
            sprite.addControl(enemyControl);

        } else if (item.equals("enemy-2")) {
            sprite = new AnimatedSprite("enemy", 1.5f, 1.5f, 5, 5, 1);
            sprite.setMaterial(enemiesMaterial);
            sprite.showIndex(5);
            sprite.flipCoords(true);
            sprite.flipHorizontal(false);
            sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
            ((AnimatedSprite) sprite).addAnimation(new Animation("idle", 5, 5, 15));
            ((AnimatedSprite) sprite).addAnimation(new Animation("walk", 5, 8, 30));
            sprite.move(0, 0.1f, 0.02f);

            Sprite shadow = new Sprite("shadow", 1.75f, 1.75f);
            shadow.setMaterial(getBaseApplication().getAssetManager().loadMaterial("Materials/shadow.j3m"));
            shadow.setQueueBucket(RenderQueue.Bucket.Transparent);
            sprite.attachChild(shadow);
            shadow.move(0, -0.1f, -0.1f);

            RigidBodyControl rbc = new RigidBodyControl(new CircleCollisionShape(0.5f), 1);
            rbc.setGravityScale(0);
            rbc.setLinearDamping(3);
            sprite.addControl(rbc);

            sprite.addControl(new ShootingEnemyControl(this, rbc));

        } else if (item.equals("enemy-3")) {
//            SpriteUtils.addImage(sprite, SHAPE, started);
            sprite = new AnimatedSprite("enemy", 1.5f, 1.5f, 5, 5, 1);
            sprite.setMaterial(enemiesMaterial);
            sprite.showIndex(10);
            sprite.flipCoords(true);
            sprite.flipHorizontal(false);
            sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
            sprite.move(0, 0, 0.02f);
            ((AnimatedSprite) sprite).addAnimation(new Animation("idle", 10, 13, 15));
            ((AnimatedSprite) sprite).addAnimation(new Animation("walk", 10, 13, 10));

            Sprite shadow = new Sprite("shadow", 0.5f, 0.5f);
            shadow.setMaterial(getBaseApplication().getAssetManager().loadMaterial("Materials/shadow.j3m"));
            shadow.setQueueBucket(RenderQueue.Bucket.Transparent);
            sprite.attachChild(shadow);
            shadow.move(0, 0.1f, 0);

            RigidBodyControl rbc = new RigidBodyControl(new CircleCollisionShape(0.3f), 1);
            rbc.setGravityScale(0);
            rbc.setLinearDamping(3);
            sprite.addControl(rbc);

            EnemyControl enemyControl = new EnemyControl(this, rbc);
            enemyControl.setDieEffect("explode-bee");
            enemyControl.setWalkSound("bee");
            enemyControl.setDamageKickback(8);
            enemyControl.setDetectDistance(5);
            enemyControl.setWalkSpeed(3);
            enemyControl.setHealth(1);
            sprite.addControl(enemyControl);
        }

        return sprite;
    }

    @Override
    public void close() {

        getBaseApplication().getSoundManager().stopMusic("spider");
        getBaseApplication().getSoundManager().stopMusic("giant-walk");
        getBaseApplication().getSoundManager().stopMusic("bee");
        getBaseApplication().getSoundManager().stopMusic("walk");
        getBaseApplication().getSoundManager().stopMusic("run");

        if (fpp != null) {
            baseApplication.getViewPort().removeProcessor(fpp);

        }

        super.close(); //To change body of generated methods, choose Tools | Templates.
    }

    public void addFireCrystal(Vector3f pos) {
        log("Adding fire crystal");
        AnimatedSprite as = new AnimatedSprite("pickup-fire", 1, 1, 5, 5, 10);
        as.setMaterial(pickupsMaterial);
        as.flipCoords(true);
        as.flipHorizontal(false);
        as.setQueueBucket(RenderQueue.Bucket.Transparent);
        as.addAnimation(new Animation("show", 0, 4, 12f));
        as.addAnimation(new Animation("idle", 5, 9, 10f));
        as.addAnimation(new Animation("done", 0, 0, 10f));
        as.setLocalTranslation(pos.x, pos.y, 0f);
        PickupFireControl control = new PickupFireControl(this, as, 1);
        as.addControl(control);
        addPickup(as);

    }

    public void addSpeedCrystal(Vector3f pos) {
        log("Adding speed crystal");
        AnimatedSprite as = new AnimatedSprite("pickup-speed", 1, 1, 5, 5, 10);
        as.setMaterial(pickupsMaterial);
        as.flipCoords(true);
        as.flipHorizontal(false);
        as.setQueueBucket(RenderQueue.Bucket.Transparent);
        as.addAnimation(new Animation("show", 10, 14, 12f));
        as.addAnimation(new Animation("idle", 15, 19, 10f));
        as.addAnimation(new Animation("done", 10, 0, 10f));
        as.setLocalTranslation(pos.x, pos.y, 0f);
        PickupSpeedControl control = new PickupSpeedControl(this, as, 10);
        as.addControl(control);
        addPickup(as);

    }

    public boolean hasEnemy() {
        boolean enemy = false;
        for (int i = 0; i < getTileMap().getTiles().size(); i++) {
            Tile tile = getTileMap().getTiles().get(i);
            if (tile.getUid().startsWith("enemy")) {
                log("Found enemy");
                enemy = true;
                break;

            }

        }

        return enemy;
    }

}
