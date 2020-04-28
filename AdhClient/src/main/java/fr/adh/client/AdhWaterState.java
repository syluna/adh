package fr.adh.client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.LowPassFilter;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

public class AdhWaterState extends BaseAppState {

	private WaterFilter water;
	private AudioNode waves;
	private float waterHeight = 0.0f;
	private final float initialWaterHeight = 90f;
	private boolean underWater = false;
	private float time = 0.0f;

	private final Vector3f lightDirection;

	public AdhWaterState(final Vector3f lightDirection) {
		this.lightDirection = lightDirection;
	}

	@Override
	protected void initialize(Application app) {
		SimpleApplication application = (SimpleApplication) app;
		// Water Filter
		water = new WaterFilter(application.getRootNode(), lightDirection);
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
				(Texture2D) application.getAssetManager().loadTexture("Common/MatDefs/Water/Textures/foam.jpg"));
		water.setRefractionStrength(0.2f);
		water.setWaterHeight(initialWaterHeight);

		// Bloom Filter
		BloomFilter bloom = new BloomFilter();
		bloom.setExposurePower(55);
		bloom.setBloomIntensity(1.0f);

		// Light Scattering Filter
		LightScatteringFilter lsf = new LightScatteringFilter(lightDirection.mult(-3000));
		lsf.setLightDensity(0.5f);

		// Depth of field Filter
		DepthOfFieldFilter dof = new DepthOfFieldFilter();
		dof.setFocusDistance(0);
		dof.setFocusRange(100);

		FilterPostProcessor fpp = new FilterPostProcessor(application.getAssetManager());
		// fpp.addFilter(water);
		fpp.addFilter(bloom);
		fpp.addFilter(dof);
		fpp.addFilter(lsf);
		fpp.addFilter(new FXAAFilter());

		int numSamples = application.getContext().getSettings().getSamples();
		if (numSamples > 0) {
			fpp.setNumSamples(numSamples);
		}

		underWater = application.getCamera().getLocation().y < waterHeight;

		waves = new AudioNode(application.getAssetManager(), "Sound/Environment/Ocean Waves.ogg",
				AudioData.DataType.Buffer);
		waves.setLooping(true);
		waves.setReverbEnabled(true);
		if (underWater) {
			waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
		} else {
			waves.setDryFilter(new LowPassFilter(1, 1));
		}
		application.getAudioRenderer().playSource(waves);
		application.getViewPort().addProcessor(fpp);
	}

	@Override
	protected void cleanup(Application app) {

	}

	@Override
	protected void onEnable() {

	}

	@Override
	protected void onDisable() {

	}

	@Override
	public void update(final float tpf) {
		super.update(tpf);
		time += tpf;
		waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
		water.setWaterHeight(initialWaterHeight + waterHeight);
		if (water.isUnderWater() && !underWater) {
			waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
			underWater = true;
		}
		if (!water.isUnderWater() && underWater) {
			underWater = false;
			// waves.setReverbEnabled(false);
			waves.setDryFilter(new LowPassFilter(1, 1f));
		}
	}

}
