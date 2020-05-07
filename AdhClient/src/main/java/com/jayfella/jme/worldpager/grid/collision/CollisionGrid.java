package com.jayfella.jme.worldpager.grid.collision;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayfella.jme.worldpager.core.CellSize;
import com.jayfella.jme.worldpager.core.GridPos2i;
import com.jayfella.jme.worldpager.world.World;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;

import lombok.Getter;

/**
 * A pool of RigidBodies shared by many consumers which avoids multiples of the
 * same rigidbodies being generated and added to bullet by multiple colliders.
 *
 * For example, if two characters are standing near each other, they will use
 * the same ridigbodies instead of generating their own individually.
 */
public abstract class CollisionGrid extends BaseAppState {

    // private static final Logger log =
    // LoggerFactory.getLogger(CollisionGrid.class);

    // private final Application app;
    private final World world;
    private final PhysicsSpace physicsSpace;

    private CellSize cellSize;

    // we have our own executor here because collisions don't want to be swamped
    // behind any scene generation tasks.
//    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    @Getter
    private boolean isReady = false;

    private final BiConsumer<CollidableGridCell, Throwable> consumer;

    private final Map<GridPos2i, RigidBodyControl> pooledRigidBodies = new HashMap<>();
    private final Set<GridPos2i> requiredPositions = new HashSet<>();

    private String name;

    public CollisionGrid(World world, PhysicsSpace physicsSpace, CellSize cellSize) {

        this.world = world;
        this.physicsSpace = physicsSpace;
        this.consumer = createConsumer();

        setCellSize(cellSize);

        this.name = "Collision Grid";
    }

    public CellSize getCellSize() {
        return cellSize;
    }

    public void setCellSize(CellSize cellSize) {
        this.cellSize = cellSize;
        pooledRigidBodies.clear();
    }

    public World getWorld() {
        return world;
    }

    public void refresh() {
        setCellSize(getCellSize());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private BiConsumer<CollidableGridCell, Throwable> createConsumer() {
        return (gridSection, throwable) -> {

            if (throwable == null) {
                world.getApplication().enqueue(() -> {
                    addRigidBody(gridSection.getGridPos(), gridSection.getResult());
                });
            } else {
                throwable.printStackTrace();
                // log.error("Error creating collision cell", throwable);
            }
        };
    }

    private void addRigidBody(GridPos2i gridPos, RigidBodyControl rigidBodyControl) {

        if (rigidBodyControl != null) {
            rigidBodyControl.setPhysicsLocation(gridPos.toWorldTranslation());
            this.pooledRigidBodies.put(gridPos, rigidBodyControl);
            this.physicsSpace.add(rigidBodyControl);
        }

    }

    public boolean positionGenerated(GridPos2i gridPos2i) {
        return pooledRigidBodies.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(gridPos2i) && entry.getValue() != null);
    }

    /**
     * Returns true if all requested positions have been generated.
     * 
     * @param positions the requested positions.
     * @return true if all positions are available, else false.
     */
    public boolean positionsGenerated(Collection<GridPos2i> positions) {
        return pooledRigidBodies.entrySet().stream()
                .filter(entry -> positions.contains(entry.getKey()) && entry.getValue() != null)
                .count() == positions.size();

    }

    public void addRequiredPositions(Collection<GridPos2i> requiredPositions) {
        this.requiredPositions.addAll(requiredPositions);
    }

    public void positionRequested(GridPos2i gridPos) {
        if (!pooledRigidBodies.containsKey(gridPos)) {
            pooledRigidBodies.put(gridPos, null);
            CompletableFuture.supplyAsync(new CollidableGridCell(gridPos, this), executor).whenComplete(consumer);
        }
    }

    public abstract RigidBodyControl positionRequestedAsync(GridPos2i gridPos);

    @Override
    protected void cleanup(Application app) {
        executor.shutdownNow();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CollisionGrid.class);

    @Override
    public void update(float tpf) {
        if (!isReady && pooledRigidBodies.size() > 0 && executor.getActiveCount() == 0
                && executor.getQueue().size() == 0) {
            LOGGER.info("Terrain is now solid.");
            isReady = true;
        }

        pooledRigidBodies.entrySet().removeIf(entry -> {
            if (requiredPositions.size() > 0 && !requiredPositions.contains(entry.getKey())) {
                this.physicsSpace.remove(entry.getValue());
                return true;
            }
            return false;
        });
        this.requiredPositions.clear();
    }

    @Override
    public String toString() {
        return getName();
    }

}
