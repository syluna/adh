package fr.adh.client;

import com.jme3.app.ChaseCameraAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData.DataType;
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
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.ClientStateListener.DisconnectInfo;
import com.jme3.network.Network;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import com.jme3.water.WaterFilter;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import fr.adh.client.gui.StartScreenController;
import fr.adh.common.ChatMessage;
import fr.adh.common.ShutdownServerMessage;
import java.io.IOException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;


public class AdHClient extends SimpleApplication implements ClientStateListener, ActionListener, ScreenController  {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdHClient.class);

    private final Vector3f lightDir = new Vector3f(-4.9236743f, -1.27054665f, 5.896916f);
    
    private Nifty nifty;
    TerrainQuad terrain;
    Material matRock;

    // Water (Ocean)
    private WaterFilter water;
    AudioNode waves;
    Geometry box;
    LowPassFilter underWaterAudioFilter = new LowPassFilter(0.5f, 0.1f);
    LowPassFilter underWaterReverbFilter = new LowPassFilter(0.5f, 0.1f);
    LowPassFilter aboveWaterAudioFilter = new LowPassFilter(1, 1);
    private float time = 0.0f;
    private float waterHeight = 0.0f;
    private final float initialWaterHeight = 90f;
    private boolean uw = false;

    // Physique
    private BulletAppState bulletAppState;
    private CharacterControl player;
    private Node playerNode;
    private BitmapText hudText;

    boolean rotate = false;
    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 0);
    boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false,
            leftRotate = false, rightRotate = false;

    private Client client;
    @Getter
    private StartScreenController startScreenController;
    
    private static AdHClient adhClient;
    
    public static final AdHClient getInstance(){
        if(adhClient == null){
            adhClient = new AdHClient();
        }
        return adhClient;
    }
    
    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        LOGGER.info("Starting application...");

        AppSettings settings = new AppSettings(true);
        settings.setTitle("L'Aube des Heros - v1.0.0");
        settings.setResolution(800, 600);
        settings.setVSync(true);

        
        getInstance().setSettings(settings);
        getInstance().setShowSettings(false);
        getInstance().setDisplayFps(false);
        getInstance().setDisplayStatView(false);
        getInstance().start();
    }
    
    public static final void sendMessage(String message){
        getInstance().client.send(new ChatMessage("Player", message));
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        
        createGUI();
        createConnection();
        
        setupKeys();
        //createTerrain();
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
/*
        time += tpf;
        waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
        water.setWaterHeight(initialWaterHeight + waterHeight);
        if (water.isUnderWater() && !uw) {

            waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
            uw = true;
        }
        if (!water.isUnderWater() && uw) {
            uw = false;
            //waves.setReverbEnabled(false);
            waves.setDryFilter(new LowPassFilter(1, 1f));
            //waves.setDryFilter(new LowPassFilter(1,1f));
        }

        // View player camera
        Vector3f camDir = cam.getDirection().mult(0.2f);
        Vector3f camLeft = cam.getLeft().mult(0.2f);
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
*/
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
  
    private void createTerrain() {
        Node mainScene = new Node("Main Scene");
        rootNode.attachChild(mainScene);
        bulletAppState = new BulletAppState();
        //bulletAppState.setDebugEnabled(true);
        stateManager.attach(bulletAppState);
        
        matRock = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        matRock.setBoolean("useTriPlanarMapping", false);
        matRock.setFloat("Shininess", 0.0f);
        matRock.setBoolean("WardIso", true);
        matRock.setTexture("AlphaMap", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap", grass);
        matRock.setFloat("DiffuseMap_0_scale", 64);
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_1", dirt);
        matRock.setFloat("DiffuseMap_1_scale", 16);
        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        rock.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_2", rock);
        matRock.setFloat("DiffuseMap_2_scale", 128);
        Texture normalMap0 = assetManager.loadTexture("Textures/Terrain/splat/grass_normal.jpg");
        normalMap0.setWrap(WrapMode.Repeat);
        Texture normalMap1 = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
        normalMap1.setWrap(WrapMode.Repeat);
        Texture normalMap2 = assetManager.loadTexture("Textures/Terrain/splat/road_normal.png");
        normalMap2.setWrap(WrapMode.Repeat);
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
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) );
        terrain.setMaterial(matRock);
        terrain.setLocalScale(new Vector3f(5, 5, 5));
        terrain.setLocalTranslation(new Vector3f(0, 30, 0));

        terrain.setShadowMode(ShadowMode.Receive);
        terrain.addControl(new RigidBodyControl(0));


        mainScene.attachChild(terrain);
        bulletAppState.getPhysicsSpace().add(terrain);

        // Player configuration
        player = new CharacterControl(new CapsuleCollisionShape(0.5f, 1.8f), .1f);
        player.setJumpSpeed(20);
        player.setFallSpeed(50);

        playerNode = new Node("Character Node");
        Spatial model = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
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
        hudText.setColor(new ColorRGBA( 0, 1, 1, 1 ));
        hudText.setText("Joueur");

        //hudText.setLocalTranslation(cam.getScreenCoordinates(playerNode.getLocalTranslation().add(-0.5f, 3f, 0f)));
        float textWidth = hudText.getLineWidth() + 20;
        float textOffset = textWidth / 2;
        hudText.setBox( new Rectangle(-textOffset, 0, textWidth, hudText.getHeight()) );
        hudText.setAlignment( BitmapFont.Align.Center );
        hudText.setQueueBucket( RenderQueue.Bucket.Transparent );
        BillboardControl bc = new BillboardControl();
        bc.setAlignment( BillboardControl.Alignment.Screen );
        hudText.addControl(bc);
        
        Node textNode = new Node( "LabelNode" );
        textNode.setLocalTranslation( 0, 2.6f + hudText.getHeight(), 0 );
        textNode.attachChild( hudText );
        
        playerNode.attachChild(textNode);
        
        //create the camera
        ChaseCameraAppState chaseCam = new ChaseCameraAppState();
        chaseCam.setTarget(playerNode);
        getStateManager().attach(chaseCam);
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

        flyCam.setEnabled(false);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(1f));
        mainScene.addLight(sun);

        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
        mainScene.addLight(al);

        Spatial sky = SkyFactory.createSky(assetManager,
                "Scenes/Beach/FullskiesSunset0068.dds", EnvMapType.CubeMap);
        sky.setLocalScale(350);
        mainScene.attachChild(sky);

        //Water Filter
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
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
        water.setRefractionStrength(0.2f);
        water.setWaterHeight(initialWaterHeight);

        //Bloom Filter
        BloomFilter bloom = new BloomFilter();
        bloom.setExposurePower(55);
        bloom.setBloomIntensity(1.0f);

        //Light Scattering Filter
        LightScatteringFilter lsf = new LightScatteringFilter(lightDir.mult(-300));
        lsf.setLightDensity(0.5f);

        //Depth of field Filter
        DepthOfFieldFilter dof = new DepthOfFieldFilter();
        dof.setFocusDistance(0);
        dof.setFocusRange(100);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        fpp.addFilter(water);
        fpp.addFilter(bloom);
        fpp.addFilter(dof);
        fpp.addFilter(lsf);
        fpp.addFilter(new FXAAFilter());

        int numSamples = getContext().getSettings().getSamples();
        if (numSamples > 0) {
            fpp.setNumSamples(numSamples);
        }

        uw = cam.getLocation().y < waterHeight;

        waves = new AudioNode(assetManager, "Sound/Environment/Ocean Waves.ogg",
                DataType.Buffer);
        waves.setLooping(true);
        waves.setReverbEnabled(true);
        if (uw) {
            waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
        } else {
            waves.setDryFilter(aboveWaterAudioFilter);
        }
        audioRenderer.playSource(waves);
        //  
        viewPort.addProcessor(fpp);
    }
  
    private void createGUI(){
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/start/start.xml", "start");
        
        startScreenController = (StartScreenController) nifty.getScreen("start").getScreenController();
        LOGGER.info("StartScreenController [{}]", startScreenController);
        //nifty.fromXml("all/intro.xml", "start");
        guiViewPort.addProcessor(niftyDisplay);
        inputManager.setCursorVisible(true);
    }
    
    @Override
    public void clientConnected(Client client) {
        LOGGER.info("Client [{}] is now connected on [{}] in version [{}].", client.getId(), client.getGameName(), client.getVersion());
    }

    @Override
    public void clientDisconnected(Client client, DisconnectInfo arg1) {
        LOGGER.info("Client now disconnecting.");
    }

    private void setupKeys() {
        inputManager.addMapping("Strafe Left",
                new KeyTrigger(KeyInput.KEY_Q),
                new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("Strafe Right",
                new KeyTrigger(KeyInput.KEY_E),
                new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("Rotate Left",
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Rotate Right",
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Walk Forward",
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Walk Backward",
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Jump",
                new KeyTrigger(KeyInput.KEY_SPACE),
                new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Shoot",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Strafe Left", "Strafe Right");
        inputManager.addListener(this, "Rotate Left", "Rotate Right");
        inputManager.addListener(this, "Walk Forward", "Walk Backward");
        inputManager.addListener(this, "Jump", "Shoot");
        
                inputManager.addListener((ActionListener) (String name, boolean isPressed, float tpf) -> {
            if (isPressed) {
                if (name.equals("foam1")) {
                    water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam.jpg"));
                }
                if (name.equals("foam2")) {
                    water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
                }
                if (name.equals("foam3")) {
                    water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam3.jpg"));
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
        inputManager.addMapping("foam1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("foam2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("foam3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("upRM", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("downRM", new KeyTrigger(KeyInput.KEY_PGDN));
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding == null || "true".equals("true")) {
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

    @Override
    public void bind(Nifty nifty, Screen screen) {
        System.out.println("bind( " + screen.getScreenId() + ")");
    }

    @Override
    public void onStartScreen() {
        System.out.println("onStartScreen");
    }

    @Override
    public void onEndScreen() {
        System.out.println("onEndScreen");
    }

    public void quit(){
        nifty.gotoScreen("end");
    }
    
    private void createConnection(){
     try {
            client = Network.connectToServer("Aube des heros", 1, "localhost", 8080);
            client.addClientStateListener(this);
            client.addMessageListener(new ClientMessageListener(), ShutdownServerMessage.class);
            client.addMessageListener(new ClientMessageListener(), ChatMessage.class);
            client.start();
        } catch (IOException ex) {
            LOGGER.error("Connection to server error", ex);
        }
    }
}
