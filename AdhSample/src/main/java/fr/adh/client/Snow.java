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

public class Snow extends Node {

	private ParticleEmitter points;
	private int height = 10;
	private int particlesPerSec = 8000;

	public Snow(AssetManager assetManager, int weather) {
		super("Snow");
		points = new ParticleEmitter("SnowPoints", Type.Triangle, particlesPerSec * weather);
		points.setShape(new EmitterSphereShape(Vector3f.ZERO, 100f));
		points.setLocalTranslation(new Vector3f(0f, height, 0f));
		points.setImagesX(1);
		points.setImagesY(1);
		points.setEndColor(new ColorRGBA(0.92f, 0.92f, 0.92f, 0.6f)); // red
		points.setStartColor(new ColorRGBA(1f, 1f, 1f, 0f)); // yellow
		points.setStartSize(1f);
		points.setEndSize(0.2f);
		points.setGravity(0, 1, 0);
		points.setLowLife(1f);
		points.setHighLife(10f);
		points.setParticlesPerSec(particlesPerSec * weather);
		points.getParticleInfluencer().setInitialVelocity(new Vector3f(0.0f, 2.0f, 0.0f));
		points.getParticleInfluencer().setVelocityVariation(0.3f);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/spark.png"));
		points.setMaterial(mat);
		points.setQueueBucket(RenderQueue.Bucket.Transparent);

		attachChild(points);
	}

}
