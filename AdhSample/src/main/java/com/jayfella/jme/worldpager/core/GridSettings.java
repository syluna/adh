package com.jayfella.jme.worldpager.core;

/**
 * Settings that define a grid size and behavior.
 */
public class GridSettings {

    private GridSettingsListener listener;

    private int viewDistance = 5;
    private CellSize cellSize = CellSize.Size_16;
    private int additionsPerFrame = 1;
    private int removalsPerFrame = 1;

    /**
     * Gets how many grid cells are drawn from the center in each cardinal direction.
     * @return the amount of cells that will be drawn in each cardinal direction.
     */
    public int getViewDistance() { return viewDistance; }

    /**
     * Determines how many grid cells are drawn from the center in each cardinal direction.
     * @param viewDistance the amount of cells that will be drawn in each cardinal direction.
     */
    public void setViewDistance(int viewDistance) {

        if (listener != null) {
            int oldValue = this.viewDistance;
            this.viewDistance = viewDistance;

            listener.viewDistanceChanged(oldValue, viewDistance);
        }
        else {
            this.viewDistance = viewDistance;
        }

    }

    /**
     * Gets the size of the grid cell.
     * @return the size of the grid cell.
     */
    public CellSize getCellSize() {
        return cellSize;
    }

    /**
     * Sets the size of the grid cell.
     * @param cellSize the size of the grid cell.
     */
    public void setCellSize(CellSize cellSize) {

        if (listener != null) {
            CellSize oldValue = this.cellSize;
            this.cellSize = cellSize;

            listener.cellSizeChanged(oldValue, cellSize);
        }
        else {
            this.cellSize = cellSize;
        }


    }

    /**
     * How many cells will be added to the scene per-frame.
     * @return the amount of cells that will be added per-frame.
     */
    public int getAdditionsPerFrame() {
        return additionsPerFrame;
    }

    /**
     * Determines how many cells will be added to the scene per frame.
     * Adding too many cells per-frame may cause "stutter" because there is so much data being pushed to the
     * graphics card in such a short amount of time.
     * @param additionsPerFrame the amount of cells to add per frame.
     */
    public void setAdditionsPerFrame(int additionsPerFrame) {
        this.additionsPerFrame = additionsPerFrame;
    }

    /**
     * How many cells will be removed from the scene per-frame.
     * @return the amount of cells to remove per-frame.
     */
    public int getRemovalsPerFrame() {
        return removalsPerFrame;
    }

    /**
     * Determines how many cells will be removed from the scene per frame.
     * Removing too many cells per-frame may cause "stutter" because there is too much garbage being collected in such
     * a short amount of time.
     * @param removalsPerFrame the amount of cells to remove per-frame.
     */
    public void setRemovalsPerFrame(int removalsPerFrame) {
        this.removalsPerFrame = removalsPerFrame;
    }

    public GridSettingsListener getListener() {
        return listener;
    }

    public void setListener(GridSettingsListener listener) {
        this.listener = listener;
    }
}
