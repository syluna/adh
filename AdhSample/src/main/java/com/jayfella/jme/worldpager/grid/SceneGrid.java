package com.jayfella.jme.worldpager.grid;

import com.jayfella.jme.worldpager.world.World;
import com.jayfella.jme.worldpager.core.GridPos2i;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.core.ThreadedWorker;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * A grid that generates scene geometries.
 */
public abstract class SceneGrid extends BaseAppState {

    private String name;

    private final List<Future<ThreadedWorker>> submittedTasks = new ArrayList<>();

    private final HashMap<GridPos2i, GridCell> loadedCells = new HashMap<>();
    private final HashSet<GridPos2i> cellAdditions = new HashSet<>();
    private final HashSet<GridPos2i> cellRemovals = new HashSet<>();
    private final List<GridPos2i> requiredCells = new ArrayList<>();
    private final List<GridPos2i> unneededCells = new ArrayList<>();

    private final GridPos2i lastGridPos = new GridPos2i(100, 100, 0);
    private final GridPos2i currentGridPos = new GridPos2i(0, 0, 0);

    private final List<GridPos2i> loadingCells = new ArrayList<GridPos2i>();

    // keep a count of how many cells we've added and removed per-frame.
    private int removalIterations = 0;
    private int additionIterations = 0;

    private final World world;

    private GridSettings gridSettings;

    protected Node gridNode;

    public SceneGrid(World world, GridSettings gridSettings) {
        this.world = world;
        this.gridSettings = gridSettings;

        this.lastGridPos.setBitshift(gridSettings.getCellSize().getBitshift());
        this.currentGridPos.setBitshift(gridSettings.getCellSize().getBitshift());

        this.gridNode = new Node("Sprite Grid");
    }

    public float[] extractHeightMap(GridPos2i gridPos)
    {
        int gridSize = getGridSettings().getCellSize().getSize();

        int hmapWidth = gridSize + 3;
        int hmapDepth = gridSize + 3;

        Vector3f worldPos = gridPos.toWorldTranslation();

        float[] heightmap = new float[hmapWidth * hmapDepth];

        for (int x = 0; x < hmapWidth; x++)
        {
            for (int z = 0; z < hmapDepth; z++)
            {
                heightmap[(z * hmapDepth) + x] = getWorld().getWorldNoise()
                        .evaluate(new Vector2f(
                                worldPos.x + x - 1,
                                worldPos.z + z - 1
                        ));
            }
        }

        return heightmap;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public World getWorld() {
        return world;
    }

    public Node getGridNode() {
        return gridNode;
    }

    public GridSettings getGridSettings() {
        return gridSettings;
    }

    public void refreshGrid() {
        // destroy all game objects of the cells.

        loadedCells.values().forEach(GridCell::destroy);

        // clear the list of loaded cells.
        loadedCells.clear();

        // re-set the cell size as it may have changed prior to this call.
        this.lastGridPos.setBitshift(gridSettings.getCellSize().getBitshift());
        this.currentGridPos.setBitshift(gridSettings.getCellSize().getBitshift());

        // set the location, forcing a reload to begin loading the cells.
        setLocation(currentGridPos.toWorldTranslation(), true);
    }

    public void refreshViewDistance() {
        setLocation(currentGridPos.toWorldTranslation(), true);
    }

    public void setLocation(Vector3f location) {
        setLocation(location, false);
    }

    public void setLocation(Vector3f location, boolean forceUpdate) {
        currentGridPos.set(location);

        // if the last grid position equals the current grid position, nothing needs to be done.
        // Unless we demand an update
        if (lastGridPos.equals(currentGridPos) && !forceUpdate) {
            return;
        }

        // iterate over the view distance and add each grid position to a required list regardless of whether it's loaded or not..
        for (int x = currentGridPos.getX() - gridSettings.getViewDistance(); x <= currentGridPos.getX() + gridSettings.getViewDistance(); x++) {
            for (int z = currentGridPos.getZ() - gridSettings.getViewDistance(); z <= currentGridPos.getZ() + gridSettings.getViewDistance(); z++) {
                requiredCells.add(new GridPos2i(x, z, gridSettings.getCellSize().getBitshift()));
            }
        }

        // clear the list of current cell additions.
        // if we got this far, we need an entirely new set of cells than any other previous call.
        cellAdditions.clear();

        // load cells we do need.
        // just blanket request all cells in our view distance.
        // the method that processes this list will not load any cells that already exist.
        cellAdditions.addAll(requiredCells);

        // if we remove the required cells from the loaded cells, we end up with a list of
        // cells we don't want anymore.
        unneededCells.addAll(loadedCells.keySet().stream()
                .filter(key -> !requiredCells.contains(key))
                .collect(Collectors.toList()));

        cellRemovals.addAll(unneededCells);

        // tidy up after ourselves.
        requiredCells.clear();
        unneededCells.clear();

        // set our last position to the set position.
        lastGridPos.set(currentGridPos);

        // start the co-routine to remove and generate cells.
        // if one is already running, stop it because it's invalid now.


    }

    public GridCell getLoadedCell(GridPos2i gridPos) {
        return loadedCells.get(gridPos);
    }

    public int getTotalCellCount() {
        return (gridSettings.getViewDistance() + 1 + gridSettings.getViewDistance()) * (gridSettings.getViewDistance() + 1 + gridSettings.getViewDistance());
    }

    public int getLoadedCellCount() {
        return loadedCells.values().stream()
                .mapToInt(gridCell -> (gridCell == null) ? 0 : 1).sum();
    }

    public int getAwaitingAdditions() {
        return this.cellAdditions.size();
    }

    @Override protected void initialize(Application app) { }
    @Override protected void cleanup(Application app) { }
    @Override protected void onEnable() { }
    @Override protected void onDisable() { }

    @Override
    public void update(float tpf) {

        updateThreadpool();

        while (cellRemovals.size() > 0) {
            GridPos2i pos = cellRemovals.iterator().next();
            cellRemovals.remove(pos);

            GridCell cell = loadedCells.get(pos);

            if (cell != null) {
                loadedCells.remove(pos);
                // Destroy(cell.GameObject);
                cell.destroy();

                // only iterate if we've actually removed a cell.
                removalIterations++;
            }

            // if we've removed the maximum amount this frame, wait until the next frame.
            if (removalIterations % gridSettings.getRemovalsPerFrame() == 0) {
                // yield return null;
                return;
            }
        }

        // reset the removal count.
        removalIterations = 0;

        while (cellAdditions.size() > 0) {
            GridPos2i pos = cellAdditions.iterator().next();
            cellAdditions.remove(pos);

            // if this position is loading or already loaded, ignore the cell load request.
            if (loadingCells.contains(pos) || loadedCells.containsKey(pos)) {
                continue;
            }

            loadingCells.add(pos);
            ThreadedWorker worker = new ThreadedWorker(pos, this);
            submittedTasks.add(world.getThreadPool().submit(worker));
            // worker.Start();

            additionIterations++;

            // if we've added the maximum amount this frame, wait until the next frame.
            if (additionIterations % gridSettings.getAdditionsPerFrame() == 0) {
                return;
            }
        }

        // reset the addition count.
        additionIterations = 0;

    }

    private void updateThreadpool() {

        submittedTasks.removeIf(future -> {

            if (future.isCancelled()) {
                return true;
            }

            if (future.isDone()) {

                ThreadedWorker worker;

                try {
                    worker = future.get();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }

                if (worker != null) {
                    applyCell(worker.getGridCell(), worker.getData());
                    addLoadedCell(worker.getGridCell());
                    removeLoadingCell(worker.getGridCell().getGridPos());
                }

                return true;
            }

            return false;
        });

    }

    protected void addLoadedCell(GridCell cell) {
        loadedCells.put(cell.getGridPos(), cell);
    }

    protected void removeLoadingCell(GridPos2i gridPos) {
        loadingCells.remove(gridPos);
    }

    /**
     * Builds a requested cell in a multi-threaded manner.
     * @param gridPos the grid position for the requested cell.
     * @return and object[] array of generated data.
     */
    public abstract Object[] buildCell(GridPos2i gridPos);


    public abstract void applyCell(GridCell cell, Object[] data);

    public abstract Class<? extends SceneGrid> getLayerType();
    public void setLayerType(Class<? extends SceneGrid> clazz) { }

    @Override
    public String toString() {
        return getName();
    }

}
