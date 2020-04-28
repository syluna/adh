/*
 * Copyright (c) 2012, Andreas Olofsson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jayfella.jme.atmosphere.sky;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

/**
 * Contains the parameters used in the scattering shader.
 *
 * @author Andreas
 */
public class ScatteringParameters {

    public ScatteringParameters(){}

    // Inner atmosphere radius
    private float innerRadius = 9.77501f;
    // Outer atmosphere radius
    private float outerRadius = 10.2963f;
    // Height position, in [0, 1] range, 0=InnerRadius, 1=OuterRadius
    private float heightPosition = 0.01f;
    // Rayleigh multiplier
    private float rayleighMultiplier = 0.0022f;
    // Mie multiplier
    private float mieMultiplier = 0.000675f;
    // Sun intensity
    private float sunIntensity = 20;
    // light intensity
    private float lightIntensity = 0.9f;



    // WaveLength for RGB channels
    private Vector3f waveLength = new Vector3f(0.65f, 0.57f, 0.475f);
    /// Phase function
    private float G = -0.9991f;

    // Exposure coeficient
    private float exposure = 4.0f;

    // Number of samples
    private int numberOfSamples = 4;

    // jayfella
    // ========

    private final Vector4f scatteringConstants = new Vector4f(
            rayleighMultiplier,
            rayleighMultiplier * 4 * FastMath.PI,
            mieMultiplier,
            mieMultiplier * 4 * FastMath.PI);

    private final Vector3f wavelengthsPow4 = new Vector3f(FastMath.pow(waveLength.x, 4f), FastMath.pow(waveLength.y, 4f), FastMath.pow(waveLength.z, 4f));
    private final Vector3f invPow4Wavelengths = new Vector3f(1f / wavelengthsPow4.x, 1f / wavelengthsPow4.y, 1f / wavelengthsPow4.z);

    private final Vector3f kWavelengths4PI = new Vector3f(
            invPow4Wavelengths.x * rayleighMultiplier + (mieMultiplier * 4f * FastMath.PI),
            invPow4Wavelengths.y * rayleighMultiplier + (mieMultiplier * 4f * FastMath.PI),
            invPow4Wavelengths.z * rayleighMultiplier + (mieMultiplier * 4f * FastMath.PI));

    private final float rESun = scatteringConstants.x * lightIntensity;

    private final Vector3f invPow4WavelengthsKrESun = new Vector3f(
            invPow4Wavelengths.x * rESun,
            invPow4Wavelengths.y * rESun,
            invPow4Wavelengths.z * rESun);

    private final float groundExposure = 1;

    public float getLightIntensity() {
        return lightIntensity;
    }

    public Vector4f getScatteringConstants() {
        return scatteringConstants;
    }

    public Vector3f getInvPow4WavelengthsKrESun() {
        return invPow4WavelengthsKrESun;
    }

    public Vector3f getkWavelengths4PI() {
        return kWavelengths4PI;
    }

    public float getGroundExposure() {
        return groundExposure;
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public float getOuterRadius() {
        return outerRadius;
    }

    public float getRayleighMultiplier() {
        return rayleighMultiplier;
    }

    public float getSunIntensity() {
        return sunIntensity;
    }

    public float getMieMultiplier() {
        return mieMultiplier;
    }

    public Vector3f getWaveLength() {
        return waveLength;
    }

    public float getHeightPosition() {
        return heightPosition;
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public float getG() {
        return G;
    }

    public float getExposure() {
        return exposure;
    }
}
