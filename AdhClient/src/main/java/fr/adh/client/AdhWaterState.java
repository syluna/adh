package fr.adh.client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.TranslucentBucketFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

public class AdhWaterState extends BaseAppState {

    private FilterPostProcessor fpp;
    private WaterFilter water;
    // private AudioNode waves;
    private float waterHeight = 0.0f;
    private final float initialWaterHeight = 0f;
    private boolean underWater = false;
    private float time = 0.0f;

    private final DirectionalLight lightDirection;

    public AdhWaterState(final DirectionalLight lightDirection) {
        this.lightDirection = lightDirection;
    }

    @Override
    protected void initialize(Application app) {
        SimpleApplication application = (SimpleApplication) app;
        fpp = new FilterPostProcessor(application.getAssetManager());
        // Water Filter
        water = new WaterFilter(application.getRootNode(), lightDirection.getDirection());
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
        fpp.addFilter(water);

        int numSamples = application.getContext().getSettings().getSamples();
        if (numSamples > 0) {
            fpp.setNumSamples(numSamples);
        }

        underWater = application.getCamera().getLocation().y < waterHeight;
        /*
         * waves = new AudioNode(application.getAssetManager(),
         * "Sound/Environment/Ocean Waves.ogg", AudioData.DataType.Buffer);
         * waves.setLooping(true); waves.setReverbEnabled(true); if (underWater) {
         * waves.setDryFilter(new LowPassFilter(0.5f, 0.1f)); } else {
         * waves.setDryFilter(new LowPassFilter(1, 1)); }
         * application.getAudioRenderer().playSource(waves);
         */
        FogFilter fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(155);
        fog.setFogDensity(1.0f);
        fog.setEnabled(false);
        fpp.addFilter(fog);
        // fpp.getFilter(FogFilter.class).setEnabled(false);

        DepthOfFieldFilter dofFilter = new DepthOfFieldFilter();
        dofFilter.setFocusDistance(0);
        dofFilter.setFocusRange(50);
        dofFilter.setBlurScale(1.4f);
        dofFilter.setEnabled(false);
        fpp.addFilter(dofFilter);

        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(application.getAssetManager(),
                2048, 3);
        shadowFilter.setLight(lightDirection);
        shadowFilter.setShadowZExtend(256);
        shadowFilter.setShadowZFadeLength(128);
        fpp.addFilter(shadowFilter);

        fpp.addFilter(new TranslucentBucketFilter());

        application.getViewPort().addProcessor(fpp);
        application.getRootNode().setShadowMode(ShadowMode.CastAndReceive);
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
            // waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
            underWater = true;
        }
        if (!water.isUnderWater() && underWater) {
            underWater = false;
            // waves.setReverbEnabled(false);
            // waves.setDryFilter(new LowPassFilter(1, 1f));
        }
    }

}
