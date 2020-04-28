package com.jayfella.jme.worldpager.grid;

import java.util.HashMap;

import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.world.World;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public abstract class ModelGrid extends SceneGrid {

	private final HashMap<String, Spatial> modelMap = new HashMap<>();

	public ModelGrid(World world, GridSettings gridSettings) {
		super(world, gridSettings);
	}

	public void registerModel(String key, Spatial value) {
		modelMap.put(key, value);
	}

	public Spatial getModel(String key) {
		return modelMap.get(key);
	}

	@Override
	public void applyCell(GridCell cell, Object[] data) {
		if (data.length > 0 && data[0] != null) {
			Node node = (Node) data[0];
			cell.getCellNode().attachChild(node);
			getGridNode().attachChild(cell.getCellNode());
		}
	}

	@Override
	public Class<? extends SceneGrid> getLayerType() {
		return getClass();
	}

}
