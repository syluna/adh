package fr.adh.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.jayfella.jme.atmosphere.NewAtmosphereState;
import com.jayfella.jme.worldpager.DemoWorldState;
import com.jayfella.jme.worldpager.world.WorldSettings;
import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.TranslucentBucketFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.water.WaterFilter;

public class AdhSample extends SimpleApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdhSample.class);

    public static void main(String[] args) throws IOException {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        LOGGER.info("Starting client...");

        AppSettings settings = new AppSettings(true);
        settings.setTitle("L'Aube des Heros - v1.0.0");
        settings.setResolution(800, 600);
        settings.setVSync(true);
        settings.setFullscreen(false);

        AdhSample app = new AdhSample();
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setDisplayFps(false);
        app.setDisplayStatView(false);

        app.start();
    }

    private DemoWorldState world;
    private BulletAppState physicsState;
    private NewAtmosphereState atmosphereState;
    private Player player;
    private Node spider;

    private boolean isInitialized = false;

    @Override
    public void simpleInitApp() {
        // move the camera above the ground
        cam.setLocation(new Vector3f(13.44f, 17.48f, 8.19f));
        cam.setRotation(new Quaternion(-0.0024324625f, 0.86707f, -0.0042337887f, -0.4981623f));

        // set the sky to a nice blue
        viewPort.setBackgroundColor(new ColorRGBA(0.5f, 0.6f, 0.7f, 1.0f));

        // move about a bit quicker.
        flyCam.setMoveSpeed(10);

        // Physics
        physicsState = new BulletAppState();
        physicsState.setThreadingType(ThreadingType.PARALLEL);
        physicsState.setDebugEnabled(true);
        stateManager.attach(physicsState);

        // Rain rain = new Rain(assetManager, 3);
        // rootNode.attachChild(rain);
        // Snow snow = new Snow(assetManager, 5);
        // rootNode.attachChild(snow);
        player = new Player(assetManager, new Vector3f(0f, 15f, 0f), "Toto the best");
        rootNode.attachChild(player.getNode());

        spider = createSpider();
        rootNode.attachChild(spider);

        // Create World
        WorldSettings worldSettings = new WorldSettings();
        worldSettings.setWorldName("Test Demo World");
        worldSettings.setSeed(123);
        worldSettings.setNumThreads(3);

        world = new DemoWorldState(worldSettings, physicsState.getPhysicsSpace());
        stateManager.attach(world);

        atmosphereState = new NewAtmosphereState(world.getWorldNode());
        stateManager.attach(atmosphereState);

        rootNode.addLight(atmosphereState.getDirectionalLight());
        rootNode.addLight(atmosphereState.getAmbientLight());

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        WaterFilter waterFilter = new WaterFilter();
        fpp.addFilter(waterFilter);

        int numSamples = getContext().getSettings().getSamples();
        if (numSamples > 0) {
            fpp.setNumSamples(numSamples);
        }
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

        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(assetManager, 2048, 3);
        shadowFilter.setLight(atmosphereState.getDirectionalLight());
        shadowFilter.setShadowZExtend(256);
        shadowFilter.setShadowZFadeLength(128);
        fpp.addFilter(shadowFilter);

        fpp.addFilter(new TranslucentBucketFilter());
        viewPort.addProcessor(fpp);

        rootNode.setShadowMode(ShadowMode.CastAndReceive);
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        if (!isInitialized && world.isReady()) {
            isInitialized = true;

            physicsState.getPhysicsSpace().addAll(player.getNode());
            physicsState.getPhysicsSpace().add(player.getControl());
            physicsState.getPhysicsSpace().add(spider.getControl(BetterCharacterControl.class));
        }

        world.setFollower(cam.getLocation());
        atmosphereState.setLocation(cam.getLocation());
    }

    public Node createSpider() {
        // Node spiderNode = new Node("Spider");
        Node spiderNode = (Node) assetManager.loadModel("Models/spiderex1/spiderex1.j3o");
        spiderNode.scale(0.5f, 0.5f, 0.5f);
        spiderNode.setLocalTranslation(new Vector3f(5f, 14f/* 12.45f */, 0f));

        // CollisionShape shape = new CapsuleCollisionShape(0.55f, 0.6f,
        // PhysicsSpace.AXIS_Z);
        BetterCharacterControl humanCtrl = new BetterCharacterControl(0.55f, 2.6f, 0.1f);
        spiderNode.addControl(humanCtrl);

        Spatial source = spiderNode.getChild("spideRex.head");
        Spatial target = AnimMigrationUtils.migrate(source);
        AnimComposer animComposer = target.getControl(AnimComposer.class);
        // animComposer.getAnimClips().forEach(animClip -> LOGGER.info("Spider AnimClip
        // name [{}].", animClip.getName()));
        AnimClip animClip = animComposer.getAnimClip("Idle");
        animComposer.setCurrentAction(animClip.getName());

        BitmapText hudText = new BitmapText(assetManager.loadFont("Interface/Fonts/Default.fnt"), false);
        hudText.setSize(0.5f);
        hudText.setColor(new ColorRGBA(0, 1, 1, 1));
        hudText.setText("Spider");

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
        textNode.setLocalTranslation(0f, 2.6f + hudText.getHeight(), 0);
        textNode.attachChild(hudText);
        spiderNode.attachChild(textNode);

        return spiderNode;
    }

}