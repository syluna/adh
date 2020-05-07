package fr.adh.client;

import java.util.Random;

import com.jayfella.fastnoise.FastNoise;
import com.jayfella.fastnoise.GradientPerturb;
import com.jayfella.fastnoise.LayerMask;
import com.jayfella.fastnoise.LayeredNoise;
import com.jayfella.fastnoise.NoiseLayer;
import com.jayfella.jme.plotters.meshplotter.MeshPlotterSettings;
import com.jayfella.jme.worldpager.core.CellSize;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.core.NoiseEvaluator;
import com.jayfella.jme.worldpager.grid.GridCollider;
import com.jayfella.jme.worldpager.grid.SceneGrid;
import com.jayfella.jme.worldpager.grid.SpriteGrid;
import com.jayfella.jme.worldpager.grid.TerrainGrid;
import com.jayfella.jme.worldpager.grid.collision.TerrainCollisionGrid;
import com.jayfella.jme.worldpager.world.AbstractWorldState;
import com.jayfella.jme.worldpager.world.WorldSettings;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;

import fr.adh.client.world.grass.GrassPathsRule;
import fr.adh.client.world.tree.PlottedModel;
import fr.adh.client.world.tree.TreesGrid;
import lombok.Getter;

public class AdhWorldState extends AbstractWorldState {

    @Getter
    private LayeredNoise layeredNoise;

    private GridCollider gridCollider;
    private TerrainCollisionGrid terrainCollisionGrid;
    private PhysicsSpace physicsSpace;

    public AdhWorldState(WorldSettings worldSettings, PhysicsSpace physicsSpace) {
        super(worldSettings);
        this.physicsSpace = physicsSpace;
        createWorldNoise();
    }

    private void createWorldNoise() {
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
        TerrainGrid terrainGrid = createTerrainGrid();
        terrainGrid.setMaterial(createTerrainMaterial(app.getAssetManager()));
        addSceneGrid(terrainGrid);

        terrainCollisionGrid = new TerrainCollisionGrid(this, physicsSpace,
                terrainGrid.getGridSettings().getCellSize());
        addCollisionGrid(terrainCollisionGrid);
        gridCollider = new GridCollider(terrainGrid.getGridSettings(), terrainCollisionGrid);

        // add a grass layer.
        SceneGrid grassGrid = createGrassGrid(app.getAssetManager());
        addSceneGrid(grassGrid);
        // add a flowers layer
        SceneGrid flowersGrid = createFlowersGrid(app.getAssetManager());
        addSceneGrid(flowersGrid);
        // add trees layer
        SceneGrid treesGrid = createTreesGrid(app.getAssetManager());
        addSceneGrid(treesGrid);
    }

    @Override
    public void setFollower(Vector3f follower) {
        super.setFollower(follower);
        gridCollider.setLocation(follower);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        gridCollider.update();
    }

    public boolean isReady() {
        return terrainCollisionGrid != null && terrainCollisionGrid.isReady();
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

    private TerrainGrid createTerrainGrid() {
        GridSettings gridSettings = new GridSettings();
        gridSettings.setCellSize(CellSize.Size_64); // the size of our grid cells.
        gridSettings.setViewDistance(3);

        return new TerrainGrid(this, gridSettings);
    }

    private SceneGrid createTreesGrid(AssetManager assetManager) {
        GridSettings gridSettings = new GridSettings();
        gridSettings.setCellSize(CellSize.Size_64);
        gridSettings.setViewDistance(4);

        // tree 1
        PlottedModel tree_1 = new PlottedModel("Fir 1", assetManager.loadModel("Models/Fir1/fir1_androlo.j3o"));
        tree_1.setMinRadius(10);
        tree_1.setMaxRadius(12);
        tree_1.setMinSpaceBetween(16);
        tree_1.setMinScale(6);
        tree_1.setMaxScale(10);
        tree_1.setMinHeight(10);
        tree_1.setMaxHeight(256);
        tree_1.setLikelihood(0.1f);
        tree_1.setMaxAttempts(32);

        // tree 2
        Node oakTree = (Node) assetManager.loadModel("Models/Oak/tree_oak.j3o");
        Geometry oakTrunk = (Geometry) oakTree.getChild("oak trunk");
        oakTrunk.setMaterial(assetManager.loadMaterial("Models/Oak/Oak_Trunk.j3m"));

        Geometry oakLeaves = (Geometry) oakTree.getChild("oak leaves");
        oakLeaves.setMaterial(assetManager.loadMaterial("Models/Oak/Oak_Leaves.j3m"));

        PlottedModel tree_2 = new PlottedModel("Oak", oakTree);
        tree_2.setMinRadius(12);
        tree_2.setMaxRadius(18);
        tree_2.setMinSpaceBetween(19);
        tree_2.setMinScale(10);
        tree_2.setMaxScale(16);
        tree_2.setMinHeight(10);
        tree_2.setMaxHeight(256);
        tree_2.setLikelihood(0.3f);
        tree_2.setMaxAttempts(32);

        // tree 3
        Node mapleTree = (Node) assetManager.loadModel("Models/Maple/tree_maple.j3o");
        Geometry mapleTrunk = (Geometry) mapleTree.getChild("maple trunk");
        mapleTrunk.setMaterial(assetManager.loadMaterial("Models/Maple/Maple_Trunk.j3m"));

        Geometry mapleLeaves = (Geometry) mapleTree.getChild("maple leaves");
        mapleLeaves.setMaterial(assetManager.loadMaterial("Models/Maple/Maple_Leaves.j3m"));

        PlottedModel tree_3 = new PlottedModel("Maple", mapleTree);
        tree_3.setMinRadius(16);
        tree_3.setMaxRadius(22);
        tree_3.setMinSpaceBetween(24);
        tree_3.setMinScale(8);
        tree_3.setMaxScale(14);
        tree_3.setMinHeight(10);
        tree_3.setMaxHeight(256);
        tree_3.setLikelihood(0.4f);
        tree_3.setMaxAttempts(32);

        TreesGrid treesGrid = new TreesGrid(this, gridSettings, tree_1, tree_2, tree_3);
        treesGrid.setName("Trees");

        return treesGrid;
    }

    private SceneGrid createFlowersGrid(AssetManager assetManager) {
        GridSettings gridSettings = new GridSettings();
        gridSettings.setCellSize(CellSize.Size_32);
        gridSettings.setViewDistance(3);
        SpriteGrid flowersGrid = new SpriteGrid(this, gridSettings);
        flowersGrid.setName("Flowers");

        // create some plotter settings so we can customize the output
        MeshPlotterSettings meshPlotterSettings = new MeshPlotterSettings();
        meshPlotterSettings.setMinSize(0.2f); // the minimum size of a grass clump.
        meshPlotterSettings.setMaxSize(1.3f); // the maximum size of a grass clump.
        meshPlotterSettings.setDensity(3.6f); // how close together the grass will generate.
        meshPlotterSettings.setMinWorldHeight(10.0f); // the lowest height grass will grow (above sea level).
        meshPlotterSettings.setMinWorldHeightDeviation(1.5f); // add a bit of deviation to the min height.
        meshPlotterSettings.setMaxWorldHeight(256); // the maximum height grass will grow.
        meshPlotterSettings.setMaxWorldHeightDeviation(0.5f); // add a bit of deviation to the max height.
        flowersGrid.setPlotterSettings(meshPlotterSettings);

        flowersGrid.setMaterial(createFlowersMaterial(assetManager));

        // add some noise to the grass layer.
        NoiseLayer grassLayer_1 = new NoiseLayer("layer 1", 7654);
        grassLayer_1.setScale(new Vector2f(0.5f, 1.5f));

        NoiseLayer grassLayer_2 = new NoiseLayer("layer 1", 2345);
        grassLayer_2.setScale(new Vector2f(1.5f, 0.5f));

        flowersGrid.getNoiseGenerator().addLayer(grassLayer_1);
        flowersGrid.getNoiseGenerator().addLayer(grassLayer_2);

        // add some rules to the generator
        GrassPathsRule grassPathsRule = new GrassPathsRule(flowersGrid);
        grassPathsRule.setThreshold(0.1f);
        grassPathsRule.setSecondChance(0.1f);
        flowersGrid.setPlotterRules(grassPathsRule);

        return flowersGrid;
    }

    private Material createFlowersMaterial(AssetManager assetManager) {
        Material flowersMaterial = new Material(assetManager, "MatDefs/Vegetation-Sprite.j3md");
        flowersMaterial.setTexture("DiffuseMap",
                assetManager.loadTexture("Textures/Sprite-Vegetation/sprite-flowers.png"));
        flowersMaterial.setTexture("Noise", assetManager.loadTexture("Textures/Noise/noise-x3-512.png"));
        flowersMaterial.setFloat("AlphaDiscardThreshold", 0.65f);
        flowersMaterial.setFloat("DistanceFalloff", 320);

        return flowersMaterial;
    }

    private SceneGrid createGrassGrid(AssetManager assetManager) {
        GridSettings gridSettings = new GridSettings();
        gridSettings.setCellSize(CellSize.Size_32);
        gridSettings.setViewDistance(8);
        SpriteGrid grassGrid = new SpriteGrid(this, gridSettings);
        grassGrid.setName("Grass");

        // create some plotter settings so we can customize the output
        MeshPlotterSettings meshPlotterSettings = new MeshPlotterSettings();

        meshPlotterSettings.setMinSize(0.2f); // the minimum size of a grass clump.
        meshPlotterSettings.setMaxSize(1.3f); // the maximum size of a grass clump.

        meshPlotterSettings.setDensity(0.2f); // how close together the grass will generate.

        meshPlotterSettings.setMinWorldHeight(10.0f); // the lowest height grass will grow (above sea level).
        meshPlotterSettings.setMinWorldHeightDeviation(1.5f); // add a bit of deviation to the min height.

        meshPlotterSettings.setMaxWorldHeight(256); // the maximum height grass will grow.
        meshPlotterSettings.setMaxWorldHeightDeviation(0.5f); // add a bit of deviation to the max height.

        grassGrid.setPlotterSettings(meshPlotterSettings);

        grassGrid.setMaterial(createGrassMaterial(assetManager));

        // add some noise to the grass layer.
        NoiseLayer grassLayer_1 = new NoiseLayer("layer 1", 543);
        grassLayer_1.setScale(new Vector2f(2.5f, 1.5f));

        NoiseLayer grassLayer_2 = new NoiseLayer("layer 1", 432);
        grassLayer_2.setScale(new Vector2f(1.5f, 2.5f));

        grassGrid.getNoiseGenerator().addLayer(grassLayer_1);
        grassGrid.getNoiseGenerator().addLayer(grassLayer_2);

        // add some rules to the generator
        GrassPathsRule grassPathsRule = new GrassPathsRule(grassGrid);
        grassPathsRule.setThreshold(0.2f);
        grassPathsRule.setSecondChance(0.2f);
        grassGrid.setPlotterRules(grassPathsRule);

        return grassGrid;
    }

    private Material createGrassMaterial(AssetManager assetManager) {
        Material grassMaterial = new Material(assetManager, "MatDefs/Vegetation-Sprite.j3md");
        grassMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Sprite-Vegetation/sprite-grass.png"));
        grassMaterial.setTexture("Noise", assetManager.loadTexture("Textures/Noise/noise-x3-512.png"));
        grassMaterial.setFloat("AlphaDiscardThreshold", 0.65f);
        grassMaterial.setFloat("DistanceFalloff", 512);

        return grassMaterial;
    }
}
