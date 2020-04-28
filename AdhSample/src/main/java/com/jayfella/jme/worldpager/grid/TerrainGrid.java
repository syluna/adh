package com.jayfella.jme.worldpager.grid;

import com.jayfella.jme.worldpager.core.GridPos2i;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.core.HeightMapMesh;
import com.jayfella.jme.worldpager.world.World;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

public class TerrainGrid extends SceneGrid {

	private Material material;

	public TerrainGrid(World world, GridSettings gridSettings) {
		super(world, gridSettings);

		setName("Terrain");
		this.gridNode = new Node("SceneGrid: " + getName());
		// material = world.getRegisteredMaterial("terrain");

	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	@Override
	public Object[] buildCell(GridPos2i gridPos) {

		float[] heightmap = extractHeightMap(gridPos);
		Mesh mesh = new HeightMapMesh(heightmap);

		Geometry geometry = new Geometry("Terrain Cell: " + gridPos, mesh);
		geometry.setMaterial(material);

		return new Object[] { geometry };
	}

	@Override
	public void applyCell(GridCell cell, Object[] data) {

		Geometry geometry = (Geometry) data[0];
		cell.getCellNode().attachChild(geometry);
		getGridNode().attachChild(cell.getCellNode());
	}

	@Override
	public Class<? extends SceneGrid> getLayerType() {
		return getClass();
	}

	@Override
	protected void cleanup(Application app) {

	}

	public static int getLodLevelFromDistance(int distance) {
		switch (distance) {
		case 0:
		case 1:
			return 0;
		case 2:
			return 1;
		default:
			return 2;
		}
	}

}
