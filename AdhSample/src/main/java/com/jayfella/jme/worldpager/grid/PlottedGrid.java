package com.jayfella.jme.worldpager.grid;

import com.jayfella.fastnoise.LayeredNoise;
import com.jayfella.jme.plotters.meshplotter.MeshPlotterRule;
import com.jayfella.jme.plotters.meshplotter.MeshPlotterSettings;

public interface PlottedGrid {

    MeshPlotterSettings getPlotterSettings();
    void setPlotterSettings(MeshPlotterSettings plotterSettings);

    void setPlotterRules(MeshPlotterRule... rules);
    MeshPlotterRule[] getPlotterRules();

    void setNoiseGenerator(LayeredNoise noiseGenerator);
    LayeredNoise getNoiseGenerator();

}
