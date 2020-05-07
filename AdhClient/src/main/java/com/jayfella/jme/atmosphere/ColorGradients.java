package com.jayfella.jme.atmosphere;


import com.jme3.math.ColorRGBA;

import java.util.ArrayList;

/**
 * A list of color values used to emulate the ambient color and sun color during
 * the day, provided that the standard atmosphere is used. This system will be extended
 * when the weather system is added.
 *
 * @author Andreas
 */
public class ColorGradients {

    private ArrayList<ColorFrame> sunColorGradient;
    private ArrayList<ColorFrame> skyAmbientGradient;

    public ColorGradients() {

        skyAmbientGradient = new ArrayList<>(7);
        skyAmbientGradient.add(new ColorFrame(new ColorRGBA(0.95f, 0.95f, 0.95f, 1f), 1f));
        skyAmbientGradient.add(new ColorFrame(new ColorRGBA(0.7f, 0.7f, 0.65f, 1f), 0.625f));
        skyAmbientGradient.add(new ColorFrame(new ColorRGBA(0.6f, 0.55f, 0.4f, 1f), 0.5625f));
        skyAmbientGradient.add(new ColorFrame(new ColorRGBA(0.6f, 0.45f, 0.3f, 1f).multLocal(0.4f), 0.5f));
        skyAmbientGradient.add(new ColorFrame(new ColorRGBA(0.5f, 0.25f, 0.25f, 1f).multLocal(0.1f), 0.45f));
        skyAmbientGradient.add(new ColorFrame(new ColorRGBA(0.2f, 0.2f, 0.3f, 1f).multLocal(0.1f), 0.35f));
        skyAmbientGradient.add(new ColorFrame(new ColorRGBA(0.2f, 0.2f, 0.5f, 1f).multLocal(0.15f), 0f));

        sunColorGradient = new ArrayList<>(8);
        sunColorGradient.add(new ColorFrame(new ColorRGBA(0.95f, 0.95f, 0.95f, 1f), 1f));
        sunColorGradient.add(new ColorFrame(new ColorRGBA(0.8f, 0.8f, 0.8f, 1f), 0.75f));
        sunColorGradient.add(new ColorFrame(new ColorRGBA(0.8f, 0.75f, 0.55f, 1f).multLocal(1.3f), 0.5625f));
        sunColorGradient.add(new ColorFrame(new ColorRGBA(0.6f, 0.5f, 0.2f, 1f).multLocal(0.75f), 0.5f));
        sunColorGradient.add(new ColorFrame(new ColorRGBA(0.6f, 0.5f, 0.2f, 1f).multLocal(0.35f), 0.4725f));
        sunColorGradient.add(new ColorFrame(new ColorRGBA(0.5f, 0.5f, 0.5f, 1f).multLocal(0.15f), 0.45f));
        sunColorGradient.add(new ColorFrame(new ColorRGBA(0.2f, 0.2f, 0.25f, 1f).multLocal(0.4f), 0.3f));
        sunColorGradient.add(new ColorFrame(new ColorRGBA(0.3f, 0.3f, 0.5f, 1f).multLocal(0.2f), 0f));

        for (int i = 0; i < skyAmbientGradient.size(); i++) {
            skyAmbientGradient.get(i).color.a = 1f;
        }

        for (int i = 0; i < sunColorGradient.size(); i++) {
            sunColorGradient.get(i).color.a = 1f;
        }
    }

    /**
     * Get the sun color corresponding to the current position value p. The
     * position value is the suns height, normalized to [0,1].
     *
     * @param p The sun height in the range [0,1]
     * @return The sun color value.
     */
    public ColorRGBA getSunColor(float p) {
        return getGradientColor(p, sunColorGradient);
    }

//    public ColorRGBA getMoonColor(float p, int phase){
//        ColorRGBA moonColor = new ColorRGBA(0.7f,0.8f,1.0f,1.0f);
//
//        if(phase > 4){
//                phase = 8 - phase;
//        }
//            moonColor.multLocal(FastMath.clamp((p - 0.1f)*5f, 0f, 1f)*0.1f*(float)phase);
//            moonColor.a = 1.0f;
//        return moonColor;
//    }

    /**
     * Same as getSunColor(float p), but returns ambient color instead.
     * @param p
     * @return
     */
    public ColorRGBA getSkyAmbientColor(float p) {
        return getGradientColor(p, skyAmbientGradient);
    }

    protected ColorRGBA getGradientColor(float p, ArrayList<ColorFrame> gradient) {
        ColorFrame frame;

        int minBoundNr = 0;
        float minBoundVal = -1f;

        for (int i = 0; i < gradient.size(); i++) {
            frame = gradient.get(i);
            if (frame.value < p && frame.value > minBoundVal) {
                minBoundNr = i;
                minBoundVal = frame.value;
            }
        }

        int maxBoundNr = 0;
        float maxBoundVal = 2f;

        for (int i = 0; i < gradient.size(); i++) {
            frame = gradient.get(i);
            if (frame.value > p && frame.value < maxBoundVal) {
                maxBoundNr = i;
                maxBoundVal = frame.value;
            }
        }

        float range = maxBoundVal - minBoundVal;
        ColorRGBA col = new ColorRGBA(gradient.get(minBoundNr).color);

        if (range != 0) {
            float rangePoint = (p - minBoundVal) / range;
            col.interpolateLocal(gradient.get(maxBoundNr).color, rangePoint);
        }
        return col;
    }

    protected class ColorFrame {

        protected ColorRGBA color;
        protected float value;

        protected ColorFrame(ColorRGBA color, float value) {
            this.color = color;
            this.value = value;
        }
    }
}
