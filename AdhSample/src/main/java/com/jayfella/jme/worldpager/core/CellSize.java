package com.jayfella.jme.worldpager.core;

public enum CellSize {

    Size_2(2, 1),
    Size_4(4, 2),
    Size_8(8, 2),
    Size_16(16, 4),
    Size_32(32, 5),
    Size_64(64, 6),
    Size_128(128, 7),
    Size_256(256, 8);

    private final int size;
    private final int bitshift;

    CellSize(int size, int bitshift) {
        this.size = size;
        this.bitshift = bitshift;
    }

    public int getSize() {
        return size;
    }

    public int getBitshift() {
        return bitshift;
    }

}
