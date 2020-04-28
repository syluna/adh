package com.jayfella.worlddemo.grass;

import com.jayfella.jme.plotters.meshplotter.MeshPlotterRule;
import com.jayfella.jme.plotters.meshplotter.tri.Triangle;
import com.jayfella.jme.worldpager.grid.PlottedGrid;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

/**
 * Creates areas where grass will not grow using noise. It generally looks like "paths".
 * It also gives the grass another chance to grow in areas it shouldn't to "fill it out a bit".
 * Very useful rules for removing grass without affecting the overall look.
 */
public class GrassPathsRule implements MeshPlotterRule {

    private final PlottedGrid grassGrid;
    private float threshold = 0.2f;
    private float secondChance = 0.2f;

    public GrassPathsRule(PlottedGrid grassGrid) {
        this.grassGrid = grassGrid;
    }

    public float getThreshold() {
        return threshold;
    }

    /**
     * Sets a threshold for the noise generator. Grass will only grow if the noise generator returns a value below this.
     * @param threshold a value between 0 and 1.
     */
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public float getSecondChance() {
        return secondChance;
    }

    /**
     * Gives the grass a "second chance" to grow if the value is above the threshold.
     * @param secondChance
     */
    public void setSecondChance(float secondChance) {
        this.secondChance = secondChance;
    }

    @Override
    public boolean validate(Vector3f worldPosition, Mesh mesh, int index, Triangle tri) {

        // create areas where the grass will not grow.
        float threshold = 0.2f;

        Vector2f worldPos = new Vector2f(worldPosition.x, worldPosition.z)
                .addLocal(new Vector2f(tri.verts[0].x, tri.verts[0].z));

        if (grassGrid.getNoiseGenerator().evaluate(worldPos) < threshold) {
            return true;
        }

        // if the grass will not grow, give it another "chance" of growing, so that the "dead" areas
        // don't look so contrasting.
        float secondChance = 0.2f;
        return (FastMath.nextRandomFloat() <= secondChance);

    }

}
