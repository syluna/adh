package fr.adh.client;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;

public class Rain extends Node {

	private ParticleEmitter points;
	private int height = 400;
	private int particlesPerSec = 800;

	public Rain(AssetManager assetManager, int weather) {
		super("rain");
		points = new ParticleEmitter("rainPoints", Type.Triangle, particlesPerSec * weather);
		points.setShape(new EmitterSphereShape(Vector3f.ZERO, 100f));
		points.setLocalTranslation(new Vector3f(0f, height, 0f));
		points.getParticleInfluencer().setInitialVelocity(new Vector3f(0.0f, -1.0f, 0.0f));
		points.getParticleInfluencer().setVelocityVariation(0.1f);
		points.setImagesX(1);
		points.setImagesY(1);
		points.setGravity(0, 500 * weather, 0);
		points.setLowLife(2);
		points.setHighLife(5);
		points.setStartSize(2f);
		points.setEndSize(1f);
		points.setStartColor(new ColorRGBA(0.0f, 0.0f, 1.0f, 0.8f));
		points.setEndColor(new ColorRGBA(0.8f, 0.8f, 1.0f, 0.6f));
		points.setFacingVelocity(false);
		points.setParticlesPerSec(particlesPerSec * weather);
		points.setRotateSpeed(0.0f);
		points.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		mat.setTexture("Texture", assetManager.loadTexture("Textures/raindrop.png"));
		points.setMaterial(mat);
		points.setQueueBucket(RenderQueue.Bucket.Transparent);

		attachChild(points);
	}

}
