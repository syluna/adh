package com.jayfella.jme.worldpager.core;

import com.jme3.math.Vector2f;

/**
 * A generic noise evaluator that allows the user to use any noise generator.
 */
public abstract class NoiseEvaluator {
    public abstract float evaluate(Vector2f loc);
}
