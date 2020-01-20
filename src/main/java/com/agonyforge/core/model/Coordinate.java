package com.agonyforge.core.model;

import java.util.Arrays;

public class Coordinate {
    private int[] xyz = new int[3];

    public Coordinate(int x, int y, int z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    public Coordinate(Coordinate original) {
        setX(original.xyz[0]);
        setY(original.xyz[1]);
        setZ(original.xyz[2]);
    }

    public void setX(int x) {
        xyz[0] = x;
    }

    public int getX() {
        return xyz[0];
    }

    public void setY(int y) {
        xyz[1] = y;
    }

    public int getY() {
        return xyz[1];
    }

    public void setZ(int z) {
        xyz[2] = z;
    }

    public int getZ() {
        return xyz[2];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate that = (Coordinate) o;
        return Arrays.equals(xyz, that.xyz);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(xyz);
    }

    public String toString() {
        return String.format("(%d, %d, %d)", xyz[0], xyz[1], xyz[2]);
    }
}
