package com.jayfella.jme.worldpager.grid;

import com.jayfella.jme.worldpager.core.GridPos2i;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

public class GridCell {

    private final GridPos2i gridPos;
    private final Node cellNode;
    private final SceneGrid parent;

    private int lodLevel;
    private Mesh[] lodMeshes;

    public GridCell(GridPos2i gridPos, Node cellNode, SceneGrid parent) {
        this.gridPos = gridPos;
        this.cellNode = cellNode;
        this.parent = parent;
    }

    public GridPos2i getGridPos() {
        return gridPos;
    }

    public Node getCellNode() {
        return cellNode;
    }

    public SceneGrid getParent() {
        return parent;
    }

    public int getLodLevel() {
        return lodLevel;
    }

    public void setLodLevel(int lodLevel) {
        this.lodLevel = lodLevel;
    }

    public Mesh[] getLodMeshes() {
        return lodMeshes;
    }

    public void setLodMeshes(Mesh[] lodMeshes) {
        this.lodMeshes = lodMeshes;
    }

    public void destroy() {
        cellNode.removeFromParent();
    }

}
