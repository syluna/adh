package com.jayfella.jme.worldpager.grid.collision;

import com.jayfella.jme.worldpager.core.GridPos2i;
import com.jme3.bullet.control.RigidBodyControl;

import java.util.function.Supplier;

public class CollidableGridCell implements Supplier<CollidableGridCell> {

    private final GridPos2i gridPos;
    private final CollisionGrid collisionGrid;

    private RigidBodyControl result;

    CollidableGridCell(GridPos2i gridPos, CollisionGrid collisionGrid) {
        this.gridPos = gridPos;
        this.collisionGrid = collisionGrid;
    }

    GridPos2i getGridPos() {
        return gridPos;
    }

    RigidBodyControl getResult() {
        return result;
    }

    @Override
    public CollidableGridCell get() {
        this.result = collisionGrid.positionRequestedAsync(gridPos);
        return this;
    }

}
