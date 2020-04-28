package fr.adh.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.control.BillboardControl;

import lombok.Getter;

public class Player {

    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    @Getter
    private Node node;

    public Player(final AssetManager assetManager, Vector3f spawnPoint, String name) {
        node = (Node) assetManager.loadModel("Models/Ninja/Ninja.j3o");
        node.scale(0.02f, 0.02f, 0.02f);
        node.setLocalTranslation(spawnPoint);

        BetterCharacterControl playerControl = new BetterCharacterControl(0.8f, 3.8f, 1f);
        node.addControl(playerControl);

        AnimComposer animComposer = node.getControl(AnimComposer.class);
        // animComposer.getAnimClips().forEach(animClip -> LOGGER.info("Player AnimClip
        // name [{}].", animClip.getName()));
        animComposer.setCurrentAction("Idle2");

        if ("Player".equalsIgnoreCase(name)) {
            return;
        }

        BitmapText hudText = new BitmapText(assetManager.loadFont("Interface/Fonts/Default.fnt"), false);
        hudText.setSize(0.5f);
        hudText.setColor(new ColorRGBA(0, 1, 1, 1));
        hudText.setText(name);

//        hudText.setLocalTranslation(cam.getScreenCoordinates(model.getLocalTranslation().add(-0.5f, 3f, 0f)));
        float textWidth = hudText.getLineWidth() + 20;
        float textOffset = textWidth / 2;
        hudText.setBox(new Rectangle(-textOffset, 0, textWidth, hudText.getHeight()));
        hudText.setAlignment(BitmapFont.Align.Center);
        hudText.setQueueBucket(RenderQueue.Bucket.Transparent);
        BillboardControl bc = new BillboardControl();
        bc.setAlignment(BillboardControl.Alignment.Screen);
        hudText.addControl(bc);

        Node textNode = new Node("LabelNode");
        textNode.setLocalTranslation(0f, 8.6f + hudText.getHeight(), 0);
        textNode.attachChild(hudText);

        node.attachChild(textNode);
    }

    public Player(final AssetManager assetManager, Vector3f spawnPoint) {
        this(assetManager, spawnPoint, "Player");
    }

    public BetterCharacterControl getControl() {
        return node.getControl(BetterCharacterControl.class);
    }

    public void remove() {
        node.removeFromParent();
    }

    public void setLocation(final Vector3f location) {
        // node.setLocalTranslation(location);
        getControl().warp(location);
    }

    public Vector3f getLocation() {
        return node.getLocalTranslation();
    }
}
