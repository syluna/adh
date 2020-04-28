package com.jayfella.jme.worldpager.core;

import com.jayfella.jme.worldpager.grid.GridCell;
import com.jayfella.jme.worldpager.grid.SceneGrid;
import com.jme3.scene.Node;

import java.util.concurrent.Callable;

public class ThreadedWorker implements Callable<ThreadedWorker> {

    private final GridPos2i gridPos;
    private final SceneGrid sceneGrid;

    private Object[] data;
    private GridCell gridCell;

    public ThreadedWorker(GridPos2i gridPos, SceneGrid sceneGrid) {
        this.gridPos = gridPos;
        this.sceneGrid = sceneGrid;
    }

    public Object[] getData() { return data; }
    public GridCell getGridCell() { return gridCell; }

    @Override
    public ThreadedWorker call() {

        Node cellNode = new Node("Cell: " + gridPos.toString());
        cellNode.setLocalTranslation(gridPos.toWorldTranslation());

        gridCell = new GridCell(gridPos, cellNode, sceneGrid);
        data = sceneGrid.buildCell(gridPos);

        return this;
    }

}
