package com.jayfella.jme.worldpager.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GridPos2i implements Cloneable {
    private static final Logger log = LoggerFactory.getLogger(GridPos2i.class);
    private int x;
    private int z;
    private int bitshift;

    public GridPos2i(int bitshift) {
        this.x = this.z = 0;
        this.bitshift = bitshift;
    }

    public GridPos2i(int x, int z, int bitshift) {
        this.x = x;
        this.z = z;
        this.bitshift = bitshift;
    }

    public static GridPos2i fromWorldLocation(float x, float z, int bitshift) {
        return new GridPos2i((int) x >> bitshift, (int) z >> bitshift, bitshift);
    }

    public static GridPos2i fromWorldLocation(Vector2f worldLocation, int bitshift) {
        return new GridPos2i((int) worldLocation.x >> bitshift, (int) worldLocation.y >> bitshift, bitshift);
    }

    public static GridPos2i fromWorldLocation(Vector3f worldLocation, int bitshift) {
        return new GridPos2i((int) worldLocation.x >> bitshift, (int) worldLocation.z >> bitshift, bitshift);
    }

    public int getBitshift() {
        return bitshift;
    }

    public void setBitshift(int bitshift) {
        this.bitshift = bitshift;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public GridPos2i set(int x, int z) {
        this.x = x;
        this.z = z;
        return this;
    }

    public GridPos2i set(Vector3f in) {
        this.x = (int) in.x >> this.bitshift;
        this.z = (int) in.z >> this.bitshift;
        return this;
    }

    public GridPos2i set(GridPos2i in) {
        this.x = in.x;
        this.z = in.z;
        this.bitshift = in.bitshift;

        return this;
    }

    public Vector3f toWorldTranslation() {
        return new Vector3f(this.x << this.bitshift, 0, this.z << this.bitshift);
    }

    public int getWorldTranslationX() {
        return this.x << this.bitshift;
    }

    public int getWorldTranslationZ() {
        return this.z << this.bitshift;
    }

    public GridPos2i subtractLocal(int x, int z) {
        this.x -= x;
        this.z -= z;
        return this;
    }

    public GridPos2i subtract(int x, int z) {
        return new GridPos2i(this.x - x, this.z - z, this.bitshift);
    }

    public GridPos2i addLocal(int x, int z) {
        this.x += x;
        this.z += z;
        return this;
    }

    public GridPos2i add(int x, int z) {
        return new GridPos2i(this.x + x, this.z + z, this.bitshift);
    }

    /**
     * Get the largest distance from this cell to another cell. This check assumes
     * both GridPositions have an equal bitshift.
     * 
     * @param other the other grid position to check.
     * @return the largest distance of the x,z planes in grid cells
     */
    public int farthestDist(GridPos2i other) {

        if (this.bitshift != other.bitshift) {
            throw new IllegalArgumentException("GridPosition bitshift must be equal!");
        }

        int x = Math.abs(other.getX() - this.x);
        int z = Math.abs(other.getZ() - this.z);
        return Math.max(x, z); // get the larger of the two planes
    }

    public GridPos3i toGridPos3i() {
        return new GridPos3i(x, 0, z, bitshift);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GridPos2i)) {
            return false;
        } else {
            GridPos2i other = (GridPos2i) obj;
            return this.x == other.x && this.z == other.z;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = hash + 7 * hash + this.x;
        hash += 61 * hash + this.z;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%d,%d", this.x, this.z);
    }

    @Override
    public GridPos2i clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException var2) {
            log.error("Clone not supported.", var2);
        }

        return new GridPos2i(this.x, this.z, this.bitshift);
    }
}
