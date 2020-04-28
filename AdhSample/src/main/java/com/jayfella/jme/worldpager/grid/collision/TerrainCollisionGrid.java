package com.jayfella.jme.worldpager.grid.collision;

import com.jayfella.jme.worldpager.core.CellSize;
import com.jayfella.jme.worldpager.core.GridPos2i;
import com.jayfella.jme.worldpager.core.HeightMapMesh;
import com.jayfella.jme.worldpager.world.World;
import com.jme3.app.Application;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

public class TerrainCollisionGrid extends CollisionGrid {

    public TerrainCollisionGrid(World world, PhysicsSpace physicsSpace, CellSize cellSize) {
        super(world, physicsSpace, cellSize);
    }

    @Override
    protected void initialize(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public RigidBodyControl positionRequestedAsync(GridPos2i gridPos) {

        int hmapWidth = getCellSize().getSize() + 3;
        int hmapDepth = getCellSize().getSize() + 3;

        Vector3f worldPos = gridPos.toWorldTranslation();

        float[] heightmap = new float[hmapWidth * hmapDepth];

        for (int x = 0; x < hmapWidth; x++) {
            for (int z = 0; z < hmapDepth; z++) {
                heightmap[(z * hmapDepth) + x] = getWorld().getWorldNoise()
                        .evaluate(new Vector2f(((worldPos.x + x) - 1), ((worldPos.z + z) - 1)));
            }
        }

        Mesh heightMapMesh = new HeightMapMesh(heightmap);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(new MeshCollisionShape(heightMapMesh, true), 0);
        // rigidBodyControl.setFriction(0);

        return rigidBodyControl;

    }

}
