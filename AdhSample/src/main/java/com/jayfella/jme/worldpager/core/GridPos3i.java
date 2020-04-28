package com.jayfella.jme.worldpager.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GridPos3i implements Cloneable {
    private static final Logger log = LoggerFactory.getLogger(GridPos3i.class);
    private int x;
    private int y;
    private int z;
    private int bitshift;

    public GridPos3i(int bitshift) {
        this.x = this.y = this.z = 0;
        this.bitshift = bitshift;
    }

    public GridPos3i(int x, int y, int z, int bitshift) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.bitshift = bitshift;
    }

    public GridPos3i(GridPos3i gridPos3i) {
        this.x = gridPos3i.x;
        this.y = gridPos3i.y;
        this.z = gridPos3i.z;
        this.bitshift = gridPos3i.bitshift;

    }

    public static GridPos3i fromWorldLocation(Vector3f worldLocation, int bitshift) {
        return new GridPos3i((int) worldLocation.x >> bitshift, (int) worldLocation.y >> bitshift,
                (int) worldLocation.z >> bitshift, bitshift);
    }

    public int getX() {
        return this.x;
    }

    public GridPos3i setX(int x) {
        this.x = x;

        return this;
    }

    public int getY() {
        return this.y;
    }

    public GridPos3i setY(int y) {
        this.y = y;

        return this;
    }

    public int getZ() {
        return this.z;
    }

    public GridPos3i setZ(int z) {
        this.z = z;

        return this;
    }

    public GridPos3i set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public GridPos3i set(Vector3f in) {
        this.x = (int) in.x >> this.bitshift;
        this.y = (int) in.y >> this.bitshift;
        this.z = (int) in.z >> this.bitshift;
        return this;
    }

    public GridPos3i set(GridPos3i in) {
        this.x = in.x;
        this.y = in.y;
        this.z = in.z;
        this.bitshift = in.bitshift;

        return this;
    }

    public Vector3f toWorldTranslation() {
        return new Vector3f(this.x << this.bitshift, this.y << this.bitshift, this.z << this.bitshift);
    }

    public int getWorldTranslationX() {
        return this.x << this.bitshift;
    }

    public int getWorldTranslationY() {
        return this.y << this.bitshift;
    }

    public int getWorldTranslationZ() {
        return this.z << this.bitshift;
    }

    public GridPos3i subtractLocal(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public GridPos3i subtract(int x, int y, int z) {
        return new GridPos3i(this.x - x, this.y - y, this.z - z, this.bitshift);
    }

    public GridPos3i subtract(GridPos3i gridPos) {
        return subtract(gridPos.x, gridPos.y, gridPos.z);
    }

    public GridPos3i addLocal(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public GridPos3i add(int x, int y, int z) {
        return new GridPos3i(this.x + x, this.y + y, this.z + z, this.bitshift);
    }

    public int getBitshift() {
        return bitshift;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GridPos3i other = (GridPos3i) o;
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash += 07 * hash + this.x;
        hash += 23 * hash + this.y;
        hash += 61 * hash + this.z;
        hash += 29 * hash + this.bitshift;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%d, %d, %d", this.x, this.y, this.z);
    }

    @Override
    public GridPos3i clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("Clone not supported.", e);
        }

        return new GridPos3i(this.x, this.y, this.z, this.bitshift);
    }
}
