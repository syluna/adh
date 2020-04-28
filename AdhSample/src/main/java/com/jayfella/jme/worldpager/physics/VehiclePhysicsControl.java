package com.jayfella.jme.worldpager.physics;

import com.jayfella.jme.worldpager.core.CellSize;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.grid.GridCollider;
import com.jayfella.jme.worldpager.grid.collision.CollisionGrid;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Responsible for loading physics meshes around itself.
 */
public class VehiclePhysicsControl extends AbstractControl {

    private final VehicleControl vehicle;
    private final GridCollider gridCollider;

    private final float velocityDistMult = 0.3f;

    public VehiclePhysicsControl(VehicleControl vehicle, CollisionGrid collisionGrid) {

        this.vehicle = vehicle;

        GridSettings gridSettings = new GridSettings();
        gridSettings.setCellSize(CellSize.Size_8);
        gridSettings.setViewDistance(1);

        this.gridCollider = new GridCollider(gridSettings, collisionGrid);
    }

    @Override
    protected void controlUpdate(float tpf) {

        Vector3f linearVelocity = vehicle.getLinearVelocity();


        if (linearVelocity.x > 0 ) {
            this.gridCollider.setVd_x_r(Math.round(Math.max(this.gridCollider.getGridSettings().getViewDistance(), linearVelocity.x * velocityDistMult)));
            this.gridCollider.setVd_x_l(this.gridCollider.getGridSettings().getViewDistance());
        }
        else if (linearVelocity.x < 0) {
            this.gridCollider.setVd_x_l(Math.round(Math.max(this.gridCollider.getGridSettings().getViewDistance(), -linearVelocity.x * velocityDistMult)));
            this.gridCollider.setVd_x_r(this.gridCollider.getGridSettings().getViewDistance());
        }

        if (linearVelocity.getZ() > 0) {
            this.gridCollider.setVd_z_b(Math.round(Math.max(this.gridCollider.getGridSettings().getViewDistance(), linearVelocity.z * velocityDistMult)));
            this.gridCollider.setVd_z_f(this.gridCollider.getGridSettings().getViewDistance());
        }
        else if (linearVelocity.getZ() < 0) {
            this.gridCollider.setVd_z_f(Math.round(Math.max(this.gridCollider.getGridSettings().getViewDistance(), -linearVelocity.z * velocityDistMult)));
            this.gridCollider.setVd_z_b(this.gridCollider.getGridSettings().getViewDistance());
        }

        this.gridCollider.setLocation(getSpatial().getLocalTranslation());
        this.gridCollider.update();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

}

