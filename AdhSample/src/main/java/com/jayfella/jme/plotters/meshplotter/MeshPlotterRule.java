package com.jayfella.jme.plotters.meshplotter;

import com.jayfella.jme.plotters.meshplotter.tri.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

@FunctionalInterface
public interface MeshPlotterRule {

    boolean validate(Vector3f worldPosition, Mesh mesh, int index, Triangle tri);

}
