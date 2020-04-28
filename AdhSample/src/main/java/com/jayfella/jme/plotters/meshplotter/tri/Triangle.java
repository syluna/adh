package com.jayfella.jme.plotters.meshplotter.tri;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class Triangle {

    public Vector3f[] verts = { new Vector3f(), new Vector3f(), new Vector3f() };
    public Vector3f[] norms = { new Vector3f(), new Vector3f(), new Vector3f() };
    public Vector2f[] texes = { new Vector2f(), new Vector2f(), new Vector2f() };
    public int[] indexes = new int[3];

    public Triangle()
    {

    }



}
