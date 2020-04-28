package com.jayfella.jme.plotters.meshplotter;

import com.jme3.math.Vector3f;

public class MeshPlotPosition {

    private Vector3f pos;
    private Vector3f dir;
    private float size;

    public MeshPlotPosition(Vector3f pos, Vector3f dir, float size)
    {
        this.pos = pos;
        this.dir = dir;
        this.size = size;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getDir() {
        return dir;
    }

    public float getSize() {
        return size;
    }
}
