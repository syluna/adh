package com.jayfella.jme.worldpager.grid;

import java.util.HashSet;
import java.util.Set;

import com.jayfella.jme.worldpager.core.GridPos2i;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.grid.collision.CollisionGrid;
import com.jme3.math.Vector3f;

public class GridCollider {

    private final GridSettings gridSettings;
    private final CollisionGrid collisionGrid;

    // view distance changes with velocity
    private int vd_x_l, vd_x_r, vd_z_f, vd_z_b;

    private final Set<GridPos2i> requiredPositions = new HashSet<>();

    private final GridPos2i currentGridPos, lastGridPos;

    public GridCollider(GridSettings gridSettings, CollisionGrid collisionGrid) {
        this.gridSettings = gridSettings;
        this.collisionGrid = collisionGrid;

        this.vd_x_l = vd_x_r = vd_z_f = vd_z_b = gridSettings.getViewDistance();

        this.currentGridPos = new GridPos2i(0, 0, gridSettings.getCellSize().getBitshift());
        this.lastGridPos = new GridPos2i(-1, 0, gridSettings.getCellSize().getBitshift());
    }

    public GridSettings getGridSettings() {
        return gridSettings;
    }

    private void invalidatePosition() {
        this.lastGridPos.set(-currentGridPos.getX(), -currentGridPos.getZ());
        setLocation(currentGridPos.toWorldTranslation());
    }

    public void setVd_x_l(int vd_x_l) {

        if (this.vd_x_l == vd_x_l) {
            return;
        }

        this.vd_x_l = vd_x_l;
        invalidatePosition();
    }

    public void setVd_x_r(int vd_x_r) {

        if (this.vd_x_r == vd_x_r) {
            return;
        }

        this.vd_x_r = vd_x_r;
        invalidatePosition();
    }

    public void setVd_z_f(int vd_z_f) {

        if (this.vd_z_f == vd_z_f) {
            return;
        }

        this.vd_z_f = vd_z_f;
        invalidatePosition();
    }

    public void setVd_z_b(int vd_z_b) {

        if (this.vd_z_b == vd_z_b) {
            return;
        }

        this.vd_z_b = vd_z_b;
        invalidatePosition();
    }

    public void setLocation(Vector3f location) {
        currentGridPos.set(location);
        if (currentGridPos.equals(lastGridPos)) {
            return;
        }
        requiredPositions.clear();
        for (int x = currentGridPos.getX() - vd_x_l; x <= currentGridPos.getX() + vd_x_r; x++) {
            for (int z = currentGridPos.getZ() - vd_z_f; z <= currentGridPos.getZ() + vd_z_b; z++) {

                GridPos2i newGridPosition = new GridPos2i(x, z, gridSettings.getCellSize().getBitshift());

                collisionGrid.positionRequested(newGridPosition);
                requiredPositions.add(newGridPosition);
            }
        }
        lastGridPos.set(currentGridPos);
    }

    public void update() {
        collisionGrid.addRequiredPositions(requiredPositions);
    }

}
