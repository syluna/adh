package com.jayfella.jme.plotters.meshplotter;

public class MeshPlotterSettings {

    private float minWorldHeight = 0;
    private float maxWorldHeight = 256;

    private float minWorldHeightDeviation = 0;
    private float maxWorldHeightDeviation = 0;

    private float minSize = 0.5f;
    private float maxSize = 2.0f;

    private float maxAngle = 35.0f;
    private float density = 0.2f;

    public float getMinWorldHeight() {
        return minWorldHeight;
    }

    /**
     * Sets the minimum height the mesh plotter will plot a position.
     * @param minWorldHeight the minimum height.
     */
    public void setMinWorldHeight(float minWorldHeight) {
        this.minWorldHeight = minWorldHeight;
    }

    public float getMaxWorldHeight() {
        return maxWorldHeight;
    }

    /**
     * Sets the maximum height the mesh plotter will plot a position.
     * @param maxWorldHeight the maximum height the mesh plotter will plot a position.
     */
    public void setMaxWorldHeight(float maxWorldHeight) {
        this.maxWorldHeight = maxWorldHeight;
    }

    public float getMinSize() {
        return minSize;
    }

    /**
     * Sets the smallest height the plotter will return for a valid position.
     * @param minSize the minimum size.
     */
    public void setMinSize(float minSize) {
        this.minSize = minSize;
    }

    public float getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the largest height the plotter will return for a valid position.
     * @param maxSize the largest size.
     */
    public void setMaxSize(float maxSize) {
        this.maxSize = maxSize;
    }

    public float getMaxAngle() {
        return maxAngle;
    }

    /**
     * Sets the maximum angle a position will be plotted for.
     * @param maxAngle the maximum angle.
     */
    public void setMaxAngle(float maxAngle) {
        this.maxAngle = maxAngle;
    }

    public float getDensity() {
        return density;
    }

    /**
     * Determines how dense or close together the plotter positions can be.
     * @param density how dense or close together the plotter positions can be.
     */
    public void setDensity(float density) {
        this.density = density;
    }


    public float getMinWorldHeightDeviation() {
        return minWorldHeightDeviation;
    }

    /**
     * Set a deviation from the minimum specified world height. This creates a "rough" edge instead of a strict one.
     * @param minWorldHeightDeviation the amount of deviation
     */
    public void setMinWorldHeightDeviation(float minWorldHeightDeviation) {
        this.minWorldHeightDeviation = minWorldHeightDeviation;
    }

    public float getMaxWorldHeightDeviation() {
        return maxWorldHeightDeviation;
    }

    /**
     * Set a deviation from the maximum specified world height. This creates a "rough" edge instead of a strict one.
     * @param maxWorldHeightDeviation the amount of deviation
     */
    public void setMaxWorldHeightDeviation(float maxWorldHeightDeviation) {
        this.maxWorldHeightDeviation = maxWorldHeightDeviation;
    }

}
