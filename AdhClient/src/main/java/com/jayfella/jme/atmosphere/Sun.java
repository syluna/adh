package com.jayfella.jme.atmosphere;

import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * Created by James on 14/05/2017.
 */
public class Sun {

    private DirectionalLight sun;
    private ColorRGBA sunColor;

    public Sun() {
        this.sun = new DirectionalLight();
        this.sunColor = new ColorRGBA();
        this.sun.setName("Sun");
    }

    public DirectionalLight getLight() {
        return this.sun;
    }

    public ColorRGBA getColor() {
        return this.sunColor;
    }

    public void setColor(ColorRGBA color) {
        this.sunColor = color;
        this.sun.setColor(color);
    }

    public Vector3f getDirection() {
        return this.sun.getDirection();
    }

    public void setDirection(Vector3f direction) {
        this.sun.setDirection(direction);
    }

}
