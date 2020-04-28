package com.jayfella.jme.worldpager.world;

public class WorldSettings {

    private String worldName = "";
    private int nThreads = 2;
    private long seed = 0;

    public WorldSettings() {
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public int getNumThreads() {
        return nThreads;
    }

    public void setNumThreads(int nThreads) {
        this.nThreads = nThreads;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }
}
