package com.jayfella.jme.worldpager.grid;

import com.jayfella.jme.worldpager.world.World;
import com.jme3.math.Vector3f;

public interface Grid {

    World getWorld();

    void refreshGrid();

    void setLocation(Vector3f location);
    void setLocation(Vector3f location, boolean forceUpdate);

}
