package com.jayfella.jme.worldpager.world;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jayfella.jme.worldpager.core.NoiseEvaluator;
import com.jayfella.jme.worldpager.grid.SceneGrid;
import com.jayfella.jme.worldpager.grid.collision.CollisionGrid;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public abstract class AbstractWorldState extends BaseAppState implements World {

    private final WorldSettings worldSettings;

    private final ExecutorService threadPoolExecutor;

    // We use a global world noise so we can extract heights whenever we need them
    // in the various "layers" or the world.
    private NoiseEvaluator worldNoise;

    private List<SceneGrid> sceneGrids = new ArrayList<>();
    private List<CollisionGrid> collisionGrids = new ArrayList<>();
    private final Vector3f follower = new Vector3f();

    private final Node worldNode;

    // private final Map<String, Material> registeredMaterials = new HashMap<>();

    public AbstractWorldState(WorldSettings worldSettings) {
        this.worldSettings = worldSettings;

        this.worldNode = new Node("World: " + worldSettings.getWorldName());
        this.threadPoolExecutor = Executors.newFixedThreadPool(worldSettings.getNumThreads());
    }

    @Override
    public WorldSettings getWorldSettings() {
        return worldSettings;
    }

    @Override
    public NoiseEvaluator getWorldNoise() {
        return worldNoise;
    }

    @Override
    public void setWorldNoise(NoiseEvaluator noiseEvaluator) {
        this.worldNoise = noiseEvaluator;
    }

    @Override
    public ExecutorService getThreadPool() {
        return threadPoolExecutor;
    }

    @Override
    public Vector3f getFollower() {
        return follower;
    }

    @Override
    public void setFollower(Vector3f follower) {
        this.follower.set(follower);
    }

    /*
     * @Override public void registerMaterial(String key, Material val) {
     * registeredMaterials.put(key, val); }
     * 
     * @Override public Material getRegisteredMaterial(String key) { return
     * registeredMaterials.get(key); }
     */

    @Override
    public List<SceneGrid> getSceneGrids() {
        return sceneGrids;
    }

    @Override
    public SceneGrid getSceneGrid(String name) {
        return sceneGrids.stream().filter(grid -> grid.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void addSceneGrid(SceneGrid pagedGrid) {
        this.sceneGrids.add(pagedGrid);
        this.worldNode.attachChild(pagedGrid.getGridNode());
    }

    @Override
    public List<CollisionGrid> getCollisionGrids() {
        return collisionGrids;
    }

    @Override
    public CollisionGrid getCollisionGrid(String name) {
        return collisionGrids.stream().filter(grid -> grid.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void addCollisionGrid(CollisionGrid collisionGrid) {
        this.collisionGrids.add(collisionGrid);
    }

    public abstract void initializeWorld(Application app);

    @Override
    protected void initialize(Application app) {
        initializeWorld(app);

        sceneGrids.forEach(child -> getStateManager().attach(child));
        collisionGrids.forEach(child -> getStateManager().attach(child));
    }

    @Override
    protected void cleanup(Application app) {
        threadPoolExecutor.shutdown();
    }

    @Override
    protected void onEnable() {
        ((SimpleApplication) getApplication()).getRootNode().attachChild(worldNode);
    }

    @Override
    protected void onDisable() {
        worldNode.removeFromParent();
    }

    @Override
    public void update(float tpf) {
        sceneGrids.forEach(child -> child.setLocation(follower));
        collisionGrids.forEach(child -> child.update(tpf));
    }

    @Override
    public Node getWorldNode() {
        return worldNode;
    }

}
