package com.jayfella.jme.worldpager.grid;

import com.jayfella.fastnoise.LayeredNoise;
import com.jayfella.jme.plotters.meshplotter.MeshPlotter;
import com.jayfella.jme.plotters.meshplotter.MeshPlotterRule;
import com.jayfella.jme.plotters.meshplotter.MeshPlotterSettings;
import com.jayfella.jme.plotters.meshplotter.tri.TriangleUtils;
import com.jayfella.jme.worldpager.core.GridPos2i;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.core.HeightMapMesh;
import com.jayfella.jme.worldpager.world.World;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

public class SpriteGrid extends SceneGrid implements PlottedGrid {

    private Material material;

    private LayeredNoise layeredNoise;

    private MeshPlotterSettings plotterSettings;
    private MeshPlotterRule[] plotterRules;

    public SpriteGrid(World world, GridSettings gridSettings) {
        super(world, gridSettings);

        setPlotterSettings(new MeshPlotterSettings());
        setNoiseGenerator(new LayeredNoise());
        setName("Sprite Grid");
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public MeshPlotterSettings getPlotterSettings() {
        return plotterSettings;
    }

    @Override
    public void setPlotterSettings(MeshPlotterSettings plotterSettings) {
        this.plotterSettings = plotterSettings;
    }

    @Override
    public void setPlotterRules(MeshPlotterRule... rules) {
        this.plotterRules = rules;
    }

    @Override
    public MeshPlotterRule[] getPlotterRules() {
        return plotterRules;
    }

    @Override
    public LayeredNoise getNoiseGenerator() {
        return layeredNoise;
    }

    @Override
    public void setNoiseGenerator(LayeredNoise layeredNoise) {
        this.layeredNoise = layeredNoise;
    }

    private Geometry build(GridPos2i gridPos, Mesh parentMesh) {

        MeshPlotter plotter = new MeshPlotter(plotterSettings, plotterRules);
        //plotter.setNoise(this.noiseGenerator);
        plotter.setWorld(gridPos.toWorldTranslation());
        plotter.setMin(new Vector3f());


        int gridSize = getGridSettings().getCellSize().getSize();
        plotter.setMax(new Vector3f(gridSize, 256, gridSize));

        TriangleUtils.processTriangles(parentMesh, plotter);

        if (plotter.hasPositions()) {

            Mesh mesh = plotter.createMesh();
            Geometry geometry = new Geometry("Sprite Cell: " + gridPos, mesh);
            geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
            geometry.setShadowMode(RenderQueue.ShadowMode.Off);
            geometry.setMaterial(material);

            return geometry;
        }

        return null;
    }

    @Override
    public Object[] buildCell(GridPos2i gridPos) {

        float[] heightmap = extractHeightMap(gridPos);
        HeightMapMesh groundMeshData = new HeightMapMesh(heightmap);

        Geometry geometry = build(gridPos, groundMeshData);

        return new Object[] { geometry };
    }

    @Override
    public void applyCell(GridCell cell, Object[] data) {

        if (data != null && data.length > 0 && data[0] != null) {
            Geometry geometry = (Geometry) data[0];

            cell.getCellNode().attachChild(geometry);
            getGridNode().attachChild(cell.getCellNode());
        }


    }

    @Override
    public Class<? extends SceneGrid> getLayerType() {
        return getClass();
    }


}
