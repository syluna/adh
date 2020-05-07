package com.jayfella.jme.plotters.circleplotter;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

public class Circle {

    private final Vector2f position;
    private final float radius;
    private final ColorRGBA color;

    public Circle(Vector2f position, float radius) {
        this(position, radius, ColorRGBA.Red.clone());
    }

    public Circle(Vector2f position, float radius, ColorRGBA color) {
        this.position = position;
        this.radius = radius;
        this.color = color;
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color.set(color);
    }

    public boolean intersects(Circle other) {
        return intersects(other, 0);
    }

    public boolean intersects(Circle other, float minSpace) {
        return FastMath.abs(other.getPosition().distance(position)) < (other.getRadius() + radius + minSpace);
    }

}
