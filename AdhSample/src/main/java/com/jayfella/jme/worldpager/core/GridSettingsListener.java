package com.jayfella.jme.worldpager.core;

public interface GridSettingsListener {

    void viewDistanceChanged(int oldValue, int newValue);

    void cellSizeChanged(CellSize oldSize, CellSize newSize);
}
