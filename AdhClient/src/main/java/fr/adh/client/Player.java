package fr.adh.client;

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

	@Getter
	private Node model;

	public Player(final AssetManager assetManager, Vector3f spawnPoint, String name) {
		BetterCharacterControl playerControl = new BetterCharacterControl(1f, 5f, 1f);
		playerControl.setGravity(new Vector3f(0f, -9.81f, 0f));
		model = (Node) assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
		model.scale(0.02f, 0.02f, 0.02f);
		model.setLocalTranslation(spawnPoint);
		model.addControl(playerControl);

		setLocation(spawnPoint);

		if ("Player".equalsIgnoreCase(name)) {
			return;
		}
		BitmapText hudText = new BitmapText(assetManager.loadFont("Interface/Fonts/Default.fnt"), false);
		hudText.setSize(0.5f);
		hudText.setColor(new ColorRGBA(0, 1, 1, 1));
		hudText.setText(name);

		// hudText.setLocalTranslation(cam.getScreenCoordinates(playerNode.getLocalTranslation().add(-0.5f,
		// 3f, 0f)));
		float textWidth = hudText.getLineWidth() + 20;
		float textOffset = textWidth / 2;
		hudText.setBox(new Rectangle(-textOffset, 0, textWidth, hudText.getHeight()));
		hudText.setAlignment(BitmapFont.Align.Center);
		hudText.setQueueBucket(RenderQueue.Bucket.Transparent);
		BillboardControl bc = new BillboardControl();
		bc.setAlignment(BillboardControl.Alignment.Screen);
		hudText.addControl(bc);

		Node textNode = new Node("LabelNode");
		textNode.setLocalTranslation(0, 2.6f + hudText.getHeight(), 0);
		textNode.attachChild(hudText);
		model.attachChild(textNode);
	}

	public Player(final AssetManager assetManager, Vector3f spawnPoint) {
		this(assetManager, spawnPoint, "Player");
	}

	public BetterCharacterControl getControl() {
		return model.getControl(BetterCharacterControl.class);
	}

	public void remove() {
		model.removeFromParent();
	}

	public void setLocation(final Vector3f location) {
		getControl().warp(location);
	}

	public Vector3f getLocation() {
		return model.getLocalTranslation();
	}
}
