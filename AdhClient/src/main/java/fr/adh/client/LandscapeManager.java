package fr.adh.client;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.app.ChaseCameraAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.LowPassFilter;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;

public class LandscapeManager implements ActionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeManager.class);

	private final Vector3f lightDir = new Vector3f(-4.9236743f, -1.27054665f, 5.896916f);

	private final SimpleApplication application;

	private TerrainQuad terrain;
	private Material matRock;

	// Water (Ocean)
	private WaterFilter water;
	private AudioNode waves;
	private LowPassFilter aboveWaterAudioFilter = new LowPassFilter(1, 1);
	private float time = 0.0f;
	private float waterHeight = 0.0f;
	private final float initialWaterHeight = 90f;
	private boolean uw = false;

	private final Vector3f walkDirection = new Vector3f(0, 0, 0);
	private final Vector3f viewDirection = new Vector3f(0, 0, 0);
	boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false, leftRotate = false,
			rightRotate = false;

	// Physique
	private BulletAppState bulletAppState;
	private CharacterControl player;
	private Node playerNode;
	private BitmapText hudText;

	public LandscapeManager(@Nonnull final SimpleApplication application) {
		this.application = application;
	}

	public void create(@Nonnull Node rootNode, @Nonnull final BitmapFont guiFont) {
		Node mainScene = new Node("Main Scene");
		rootNode.attachChild(mainScene);
		bulletAppState = new BulletAppState();
		// bulletAppState.setDebugEnabled(true);
		application.getStateManager().attach(bulletAppState);

		matRock = new Material(application.getAssetManager(), "Common/MatDefs/Terrain/TerrainLighting.j3md");
		matRock.setBoolean("useTriPlanarMapping", false);
		matRock.setFloat("Shininess", 0.0f);
		matRock.setBoolean("WardIso", true);
		matRock.setTexture("AlphaMap",
				application.getAssetManager().loadTexture("Textures/Terrain/splat/alphamap.png"));
		Texture heightMapImage = application.getAssetManager().loadTexture("Textures/Terrain/splat/mountains512.png");
		Texture grass = application.getAssetManager().loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(Texture.WrapMode.Repeat);
		matRock.setTexture("DiffuseMap", grass);
		matRock.setFloat("DiffuseMap_0_scale", 64);
		Texture dirt = application.getAssetManager().loadTexture("Textures/Terrain/splat/dirt.jpg");
		dirt.setWrap(Texture.WrapMode.Repeat);
		matRock.setTexture("DiffuseMap_1", dirt);
		matRock.setFloat("DiffuseMap_1_scale", 16);
		Texture rock = application.getAssetManager().loadTexture("Textures/Terrain/splat/road.jpg");
		rock.setWrap(Texture.WrapMode.Repeat);
		matRock.setTexture("DiffuseMap_2", rock);
		matRock.setFloat("DiffuseMap_2_scale", 128);
		Texture normalMap0 = application.getAssetManager().loadTexture("Textures/Terrain/splat/grass_normal.jpg");
		normalMap0.setWrap(Texture.WrapMode.Repeat);
		Texture normalMap1 = application.getAssetManager().loadTexture("Textures/Terrain/splat/dirt_normal.png");
		normalMap1.setWrap(Texture.WrapMode.Repeat);
		Texture normalMap2 = application.getAssetManager().loadTexture("Textures/Terrain/splat/road_normal.png");
		normalMap2.setWrap(Texture.WrapMode.Repeat);
		matRock.setTexture("NormalMap", normalMap0);
		matRock.setTexture("NormalMap_1", normalMap1);
		matRock.setTexture("NormalMap_2", normalMap2);

		AbstractHeightMap heightmap = null;
		try {
			heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.25f);
			heightmap.load();
			heightmap.smooth(0.9f, 1);
		} catch (Exception e) {
			LOGGER.error("Error when loading HeightMapImage ! ", e);
		}
		terrain = new TerrainQuad("terrain", 65, 513, heightmap == null ? null : heightmap.getHeightMap());
		TerrainLodControl control = new TerrainLodControl(terrain, application.getCamera());
		control.setLodCalculator(new DistanceLodCalculator(65, 2.7f));
		terrain.setMaterial(matRock);
		terrain.setLocalScale(new Vector3f(5, 5, 5));
		terrain.setLocalTranslation(new Vector3f(0, 30, 0));

		terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
		terrain.addControl(new RigidBodyControl(0));

		mainScene.attachChild(terrain);
		bulletAppState.getPhysicsSpace().add(terrain);

		// Player configuration
		player = new CharacterControl(new CapsuleCollisionShape(0.5f, 1.8f), .1f);
		player.setJumpSpeed(20);
		player.setFallSpeed(50);

		playerNode = new Node("Character Node");
		Spatial model = application.getAssetManager().loadModel("Models/Ninja/Ninja.mesh.xml");
		model.scale(0.02f, 0.02f, 0.02f);
		model.rotate(0.0f, -3.0f, 0.0f);
		model.setLocalTranslation(0f, -1.4f, 0f);

		playerNode.addControl(player);
		bulletAppState.getPhysicsSpace().add(player);
		mainScene.attachChild(playerNode);
		playerNode.attachChild(model);
		playerNode.setModelBound(model.getWorldBound());

		player.warp(new Vector3f(-434.51205f, 115.15f, 190.11417f));

		// You can change the gravity of individual physics objects after they are
		// added to the PhysicsSpace.
		player.setGravity(new Vector3f(0, -30f, 0));

		hudText = new BitmapText(guiFont, false);
		hudText.setSize(0.5f);
		hudText.setColor(new ColorRGBA(0, 1, 1, 1));
		hudText.setText("Joueur");

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

		playerNode.attachChild(textNode);

		// create the camera
		ChaseCameraAppState chaseCam = new ChaseCameraAppState();
		chaseCam.setTarget(playerNode);
		application.getStateManager().attach(chaseCam);
		chaseCam.setInvertHorizontalAxis(true);
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setZoomSpeed(0.5f);
		chaseCam.setMinVerticalRotation(-FastMath.HALF_PI);
		chaseCam.setRotationSpeed(3);
		chaseCam.setDefaultDistance(10);
		chaseCam.setMinDistance(0.01f);
		chaseCam.setMaxDistance(20f);
		chaseCam.setZoomSpeed(0.1f);
		chaseCam.setDefaultVerticalRotation(0.3f);

		application.getFlyByCamera().setEnabled(false);

		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(lightDir);
		sun.setColor(ColorRGBA.White.clone().multLocal(1f));
		mainScene.addLight(sun);

		AmbientLight al = new AmbientLight();
		al.setColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
		mainScene.addLight(al);

		Spatial sky = SkyFactory.createSky(application.getAssetManager(), "Scenes/Beach/FullskiesSunset0068.dds",
				SkyFactory.EnvMapType.CubeMap);
		sky.setLocalScale(350);
		mainScene.attachChild(sky);

		// Water Filter
		water = new WaterFilter(mainScene, lightDir);
		water.setWaterColor(new ColorRGBA().setAsSrgb(0.0078f, 0.3176f, 0.5f, 1.0f));
		water.setDeepWaterColor(new ColorRGBA().setAsSrgb(0.0039f, 0.00196f, 0.145f, 1.0f));
		water.setUnderWaterFogDistance(80);
		water.setWaterTransparency(0.12f);
		water.setFoamIntensity(0.4f);
		water.setFoamHardness(0.3f);
		water.setFoamExistence(new Vector3f(0.8f, 8f, 1f));
		water.setReflectionDisplace(50);
		water.setRefractionConstant(0.25f);
		water.setColorExtinction(new Vector3f(30, 50, 70));
		water.setCausticsIntensity(0.4f);
		water.setWaveScale(0.003f);
		water.setMaxAmplitude(2f);
		water.setFoamTexture(
				(Texture2D) application.getAssetManager().loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
		water.setRefractionStrength(0.2f);
		water.setWaterHeight(initialWaterHeight);

		// Bloom Filter
		BloomFilter bloom = new BloomFilter();
		bloom.setExposurePower(55);
		bloom.setBloomIntensity(1.0f);

		// Light Scattering Filter
		LightScatteringFilter lsf = new LightScatteringFilter(lightDir.mult(-300));
		lsf.setLightDensity(0.5f);

		// Depth of field Filter
		DepthOfFieldFilter dof = new DepthOfFieldFilter();
		dof.setFocusDistance(0);
		dof.setFocusRange(100);

		FilterPostProcessor fpp = new FilterPostProcessor(application.getAssetManager());

		fpp.addFilter(water);
		fpp.addFilter(bloom);
		fpp.addFilter(dof);
		fpp.addFilter(lsf);
		fpp.addFilter(new FXAAFilter());

		int numSamples = application.getContext().getSettings().getSamples();
		if (numSamples > 0) {
			fpp.setNumSamples(numSamples);
		}

		uw = application.getCamera().getLocation().y < waterHeight;

		waves = new AudioNode(application.getAssetManager(), "Sound/Environment/Ocean Waves.ogg",
				AudioData.DataType.Buffer);
		waves.setLooping(true);
		waves.setReverbEnabled(true);
		if (uw) {
			waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
		} else {
			waves.setDryFilter(aboveWaterAudioFilter);
		}
		application.getAudioRenderer().playSource(waves);
		//
		application.getViewPort().addProcessor(fpp);

		setupKeys();
	}

	public void update(float tpf) {
		time += tpf;
		waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
		water.setWaterHeight(initialWaterHeight + waterHeight);
		if (water.isUnderWater() && !uw) {

			waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
			uw = true;
		}
		if (!water.isUnderWater() && uw) {
			uw = false;
			// waves.setReverbEnabled(false);
			waves.setDryFilter(new LowPassFilter(1, 1f));
			// waves.setDryFilter(new LowPassFilter(1,1f));
		}

		// View player camera
		Vector3f camDir = application.getCamera().getDirection().mult(0.2f);
		Vector3f camLeft = application.getCamera().getLeft().mult(0.2f);
		camDir.y = 0;
		camLeft.y = 0;
		viewDirection.set(camDir);
		walkDirection.set(0, 0, 0);
		if (leftStrafe) {
			walkDirection.addLocal(camLeft);
		} else if (rightStrafe) {
			walkDirection.addLocal(camLeft.negate());
		}
		if (leftRotate) {
			viewDirection.addLocal(camLeft.mult(tpf));
		} else if (rightRotate) {
			viewDirection.addLocal(camLeft.mult(tpf).negate());
		}
		if (forward) {
			walkDirection.addLocal(camDir);
		} else if (backward) {
			walkDirection.addLocal(camDir.negate());
		}
		player.setWalkDirection(walkDirection);
		player.setViewDirection(viewDirection);
	}

	private void setupKeys() {
		application.getInputManager().addMapping("Strafe Left", new KeyTrigger(KeyInput.KEY_Q),
				new KeyTrigger(KeyInput.KEY_Z));
		application.getInputManager().addMapping("Strafe Right", new KeyTrigger(KeyInput.KEY_E),
				new KeyTrigger(KeyInput.KEY_X));
		application.getInputManager().addMapping("Rotate Left", new KeyTrigger(KeyInput.KEY_A),
				new KeyTrigger(KeyInput.KEY_LEFT));
		application.getInputManager().addMapping("Rotate Right", new KeyTrigger(KeyInput.KEY_D),
				new KeyTrigger(KeyInput.KEY_RIGHT));
		application.getInputManager().addMapping("Walk Forward", new KeyTrigger(KeyInput.KEY_W),
				new KeyTrigger(KeyInput.KEY_UP));
		application.getInputManager().addMapping("Walk Backward", new KeyTrigger(KeyInput.KEY_S),
				new KeyTrigger(KeyInput.KEY_DOWN));
		application.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
		application.getInputManager().addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		application.getInputManager().addListener(this, "Strafe Left", "Strafe Right");
		application.getInputManager().addListener(this, "Rotate Left", "Rotate Right");
		application.getInputManager().addListener(this, "Walk Forward", "Walk Backward");
		application.getInputManager().addListener(this, "Jump", "Shoot");

		application.getInputManager().addListener((ActionListener) (String name, boolean isPressed, float tpf) -> {
			if (isPressed) {
				if (name.equals("foam1")) {
					water.setFoamTexture((Texture2D) application.getAssetManager()
							.loadTexture("Common/MatDefs/Water/Textures/foam.jpg"));
				}
				if (name.equals("foam2")) {
					water.setFoamTexture((Texture2D) application.getAssetManager()
							.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
				}
				if (name.equals("foam3")) {
					water.setFoamTexture((Texture2D) application.getAssetManager()
							.loadTexture("Common/MatDefs/Water/Textures/foam3.jpg"));
				}

				if (name.equals("upRM")) {
					water.setReflectionMapSize(Math.min(water.getReflectionMapSize() * 2, 4096));
					System.out.println("Reflection map size : " + water.getReflectionMapSize());
				}
				if (name.equals("downRM")) {
					water.setReflectionMapSize(Math.max(water.getReflectionMapSize() / 2, 32));
					System.out.println("Reflection map size : " + water.getReflectionMapSize());
				}
			}
		}, "foam1", "foam2", "foam3", "upRM", "downRM");
		application.getInputManager().addMapping("foam1", new KeyTrigger(KeyInput.KEY_1));
		application.getInputManager().addMapping("foam2", new KeyTrigger(KeyInput.KEY_2));
		application.getInputManager().addMapping("foam3", new KeyTrigger(KeyInput.KEY_3));
		application.getInputManager().addMapping("upRM", new KeyTrigger(KeyInput.KEY_PGUP));
		application.getInputManager().addMapping("downRM", new KeyTrigger(KeyInput.KEY_PGDN));
	}

	@Override
	public void onAction(String binding, boolean value, float tpf) {
		if (binding == null) {
			return;
		}
		switch (binding) {
		case "Strafe Left":
			leftStrafe = value;
			break;
		case "Strafe Right":
			rightStrafe = value;
			break;
		case "Rotate Left":
			leftRotate = value;
			break;
		case "Rotate Right":
			rightRotate = value;
			break;
		case "Walk Forward":
			forward = value;
			break;
		case "Walk Backward":
			backward = value;
			break;
		case "Jump":
			player.jump(new Vector3f(0f, 20f, 0f));
			break;
		default:
			break;
		}
	}
}
