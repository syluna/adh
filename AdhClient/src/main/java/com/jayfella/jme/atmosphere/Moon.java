package com.jayfella.jme.atmosphere;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

/**
 * Created by James on 14/05/2017.
 */
public class Moon {

	private Geometry moonGeom;
	private Material moonMat;
	private DirectionalLight moonLight;

	private float phase;

	public Moon(AssetManager assetManager) {

		moonLight = new DirectionalLight();
		moonLight.setName("Moon");

		Quad mq = new Quad(50, 50);
		moonGeom = new Geometry("Moon", mq);
		moonMat = new Material(assetManager, "MatDefs/Moon/MoonBase.j3md");
		Texture moonTex = assetManager.loadTexture("Textures/Moon/Moon.png");
		moonMat.setTexture("MoonTex", moonTex);
		moonMat.setFloat("Phase", 0);
		moonMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.AlphaAdditive);

		moonGeom.setMaterial(moonMat);

		moonGeom.setQueueBucket(RenderQueue.Bucket.Translucent);
		moonGeom.setShadowMode(RenderQueue.ShadowMode.Off);
	}

	public float getPhase() {
		return phase;
	}

	public void setPhase(float phase) {
		this.phase = phase;
		moonGeom.getMaterial().setFloat("Phase", phase);
	}

	public Geometry getGeometry() {
		return moonGeom;
	}

	public Material getMoonMaterial() {
		return moonMat;
	}

	public DirectionalLight getLight() {
		return moonLight;
	}

}
