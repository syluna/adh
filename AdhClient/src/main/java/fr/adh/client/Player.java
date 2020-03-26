package fr.adh.client;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import lombok.Getter;

public class Player extends Node {

	@Getter
	private Node model;
	@Getter
	private BetterCharacterControl playerControl;

	public Player(Vector3f spawnPoint) {
		playerControl = new BetterCharacterControl(1f, 5f, 1f);
		playerControl.setGravity(new Vector3f(0f, -9.81f, 0f));
		model = (Node) AdhClient.getInstance().getAssetManager().loadModel("Models/Ninja/Ninja.mesh.xml");
		model.scale(0.02f, 0.02f, 0.02f);
		model.setLocalTranslation(0f, -1.4f, 0f);
		model.addControl(playerControl);

		attachChild(model);
		setLocation(spawnPoint);
	}

	public void remove() {
		playerControl.getPhysicsSpace().remove(playerControl);
		removeFromParent();
	}

	public void setLocation(Vector3f location) {
		playerControl.warp(location);
	}

	public Vector3f getLocation() {
		return model.getLocalTranslation();
	}
}
