package fr.adh.client;

import java.util.Random;

import com.jayfella.fastnoise.FastNoise;
import com.jayfella.fastnoise.GradientPerturb;
import com.jayfella.fastnoise.LayerMask;
import com.jayfella.fastnoise.LayeredNoise;
import com.jayfella.fastnoise.NoiseLayer;
import com.jayfella.jme.worldpager.core.CellSize;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.core.NoiseEvaluator;
import com.jayfella.jme.worldpager.grid.TerrainGrid;
import com.jayfella.jme.worldpager.grid.collision.TerrainCollisionGrid;
import com.jayfella.jme.worldpager.world.AbstractWorldState;
import com.jayfella.jme.worldpager.world.WorldSettings;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;

import lombok.Getter;

public class AdhWorldState extends AbstractWorldState {

	@Getter
	private LayeredNoise layeredNoise;

	@Getter
	private BulletAppState bulletAppState;

	public AdhWorldState(WorldSettings worldSettings) {
		super(worldSettings);

		bulletAppState = new BulletAppState();

		// Create layers of noise decreasing in size.
		// We start with huge continents and work our way down to small details.
		layeredNoise = new LayeredNoise();
		layeredNoise.setHardFloor(true);
		layeredNoise.setHardFloorHeight(20);
		layeredNoise.setHardFloorStrength(0.6f);

		Random random = new Random(getWorldSettings().getSeed());
		NoiseLayer continents = new NoiseLayer("Continents");
		continents.setSeed(random.nextInt());
		continents.setStrength(200);
		continents.setScale(0.03f, 0.03f);
		continents.setFractalOctaves(1);
		layeredNoise.addLayer(continents);

		NoiseLayer mountains = new NoiseLayer("Mountains");
		mountains.setSeed(random.nextInt());
		mountains.setNoiseType(FastNoise.NoiseType.PerlinFractal);
		mountains.setStrength(512);
		mountains.setScale(0.25f, 0.25f);
		layeredNoise.addLayer(mountains);
		layeredNoise.addLayerMask(new LayerMask(mountains, continents));

		NoiseLayer hills = new NoiseLayer("Hills");
		hills.setSeed(random.nextInt());
		hills.setNoiseType(FastNoise.NoiseType.PerlinFractal);
		hills.setStrength(96);
		hills.setScale(0.07f, 0.07f);
		hills.setFrequency(0.005f);
		hills.setFractalOctaves(1);
		hills.setGradientPerturb(GradientPerturb.Fractal);
		hills.setGradientPerturbAmp(30);
		layeredNoise.addLayer(hills);
		layeredNoise.addLayerMask(new LayerMask(hills, continents));

		NoiseLayer details = new NoiseLayer("Details");
		details.setSeed(random.nextInt());
		details.setNoiseType(FastNoise.NoiseType.PerlinFractal);
		details.setStrength(15);
		details.setScale(1f, 1f);
		details.setFractalOctaves(8);

		layeredNoise.addLayer(details);

		setWorldNoise(new NoiseEvaluator() {
			@Override
			public float evaluate(Vector2f loc) {
				return layeredNoise.evaluate(loc);
			}
		});
	}

	@Override
	public void initializeWorld(final Application app) {
		GridSettings gridSettings = new GridSettings();
		gridSettings.setCellSize(CellSize.Size_64); // the size of our grid cells.
		gridSettings.setViewDistance(5);

		TerrainGrid terrainGrid = new TerrainGrid(this, gridSettings);
		terrainGrid.setMaterial(createTerrainMaterial(app.getAssetManager()));
		addSceneGrid(terrainGrid);

		TerrainCollisionGrid terrainCollisionGrid = new TerrainCollisionGrid(this, bulletAppState.getPhysicsSpace(),
				CellSize.Size_64);
		addCollisionGrid(terrainCollisionGrid);
	}

	private Material createTerrainMaterial(AssetManager assetManager) {
		final Material terrainMaterial = new Material(assetManager, "MatDefs/TrilinearLighting.j3md");

		Texture textureGrass = assetManager.loadTexture("Textures/Ground/grass.jpg");
		textureGrass.setWrap(Texture.WrapMode.Repeat);
		terrainMaterial.setTexture("DiffuseMap", textureGrass);

		Texture texture = assetManager.loadTexture("Textures/Ground/grass-flat.jpg");
		texture.setWrap(Texture.WrapMode.Repeat);
		terrainMaterial.setTexture("DiffuseMapLow", texture);

		texture = assetManager.loadTexture("Textures/Ground/brown-dirt-norm.jpg");
		texture.setWrap(Texture.WrapMode.Repeat);
		terrainMaterial.setTexture("NormalMap", texture);

		texture = assetManager.loadTexture("Textures/Ground/brown-dirt2.jpg");
		texture.setWrap(Texture.WrapMode.Repeat);
		terrainMaterial.setTexture("DiffuseMapX", texture);

		// texture = assets.loadTexture("Textures/test-norm.png");
		texture = assetManager.loadTexture("Textures/Ground/brown-dirt-norm.jpg");
		texture.setWrap(Texture.WrapMode.Repeat);
		terrainMaterial.setTexture("NormalMapX", texture);

		texture = assetManager.loadTexture("Textures/Ground/brown-dirt2.jpg");
		texture.setWrap(Texture.WrapMode.Repeat);
		terrainMaterial.setTexture("DiffuseMapZ", texture);

		// texture = assets.loadTexture("Textures/test-norm.png");
		texture = assetManager.loadTexture("Textures/Ground/brown-dirt-norm.jpg");
		texture.setWrap(Texture.WrapMode.Repeat);
		terrainMaterial.setTexture("NormalMapZ", texture);

		// Now the default down texture... we use a separate one
		// and DiffuseMap will be used for the top
		texture = assetManager.loadTexture("Textures/Ground/canvas128.jpg");
		texture.setWrap(Texture.WrapMode.Repeat);
		terrainMaterial.setTexture("DiffuseMapY", texture);

		// texture = assets.loadTexture("Textures/test-norm.png");
		texture = assetManager.loadTexture("Textures/Ground/brown-dirt-norm.jpg");
		texture.setWrap(Texture.WrapMode.Repeat);
		terrainMaterial.setTexture("NormalMapY", texture);

		// We will need a noise texture soon, might as well set it
		// now
		texture = assetManager.loadTexture("Textures/Noise/noise-x3-512.png");
		texture.setWrap(Texture.WrapMode.Repeat);
		terrainMaterial.setTexture("Noise", texture);

		return terrainMaterial;
	}
}
