package com.agonyforge.core.model;

import com.agonyforge.core.model.util.BaseEnumSetConverter;
import com.agonyforge.core.model.util.PersistentEnum;

public enum Direction implements PersistentEnum {
    NORTH(0, "north", "n", "south", 0, 1, 0),
    EAST(1, "east", "e", "west", 1, 0, 0),
    SOUTH(2, "south", "s", "north", 0, -1, 0),
    WEST(3, "west", "w", "east", -1, 0, 0),
//    NORTHWEST(4, "northwest", "nw","southeast", -1, 1, 0),
//    NORTHEAST(5, "northeast", "ne","southwest", 1, 1, 0),
//    SOUTHWEST(6, "southwest", "sw","northeast", -1, -1, 0),
//    SOUTHEAST(7, "southeast", "se","northwest", 1, -1, 0),
    UP(8, "up", "u", "down", 0, 0, 1),
    DOWN(9, "down", "d", "up", 0, 0, -1);

    private int index;
    private String name;
    private String abbreviation;
    private String opposite;
    private int x;
    private int y;
    private int z;

    Direction(
        int index,
        String name,
        String abbreviation,
        String opposite,
        int x,
        int y,
        int z) {

        this.index = index;
        this.name = name;
        this.abbreviation = abbreviation;
        this.opposite = opposite;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getOpposite() {
        return opposite;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public static class Converter extends BaseEnumSetConverter<Direction> {
        public Converter() {
            super(Direction.class);
        }
    }
}
