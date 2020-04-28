package com.jayfella.jme.worldpager.physics;

import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.grid.GridCollider;
import com.jayfella.jme.worldpager.grid.collision.CollisionGrid;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class EntityPhysicsControl extends AbstractControl {

    private final GridCollider gridCollider;

    public EntityPhysicsControl(CollisionGrid pooledCollisionGrid, GridSettings gridSettings) {
        this.gridCollider = new GridCollider(gridSettings, pooledCollisionGrid);
    }

    @Override
    protected void controlUpdate(float tpf) {
        this.gridCollider.setLocation(getSpatial().getWorldTranslation());
        this.gridCollider.update();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

}
