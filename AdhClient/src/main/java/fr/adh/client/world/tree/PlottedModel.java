package fr.adh.client.world.tree;

import com.jme3.scene.Spatial;

public class PlottedModel {

    private final String name;

    private final Spatial treeModel;

    private float minScale = 1.0f;
    private float maxScale = 2.0f;

    private float minRadius = 5.0f;
    private float maxRadius = 8.0f;

    private float minSpaceBetween = 10.0f;

    private float minHeight = 0;
    private float maxHeight = 256;

    private int maxAttempts = 100;

    // how likely this model will be placed in the loaded grid cell.
    private float likelihood = 1.0f;

    private int minAmount = 5;
    private int maxAmount = 10;

    public PlottedModel(String name, Spatial treeModel) {
        this.name = name;
        this.treeModel = treeModel;
    }

    public String getName() {
        return name;
    }

    public Spatial getTreeModel() {
        return treeModel;
    }

    public float getMinScale() {
        return minScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public float getMinRadius() {
        return minRadius;
    }

    public void setMinRadius(float minRadius) {
        this.minRadius = minRadius;
    }

    public float getMaxRadius() {
        return maxRadius;
    }

    public void setMaxRadius(float maxRadius) {
        this.maxRadius = maxRadius;
    }

    public float getMinSpaceBetween() {
        return minSpaceBetween;
    }

    public void setMinSpaceBetween(float minSpaceBetween) {
        this.minSpaceBetween = minSpaceBetween;
    }

    public float getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }

    public float getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public float getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(float likelihood) {
        this.likelihood = likelihood;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }
}
