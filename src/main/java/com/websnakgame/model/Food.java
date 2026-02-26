package com.websnakgame.model;

/**
 * 代表食物的座標與得分權重。
 */
public class Food {
    private Cell location;
    private final int value;

    public Food(Cell location, int value) {
        this.location = location;
        this.value = value;
    }

    public Cell getLocation() {
        return location;
    }

    public void setLocation(Cell location) {
        this.location = location;
    }

    public int getValue() {
        return value;
    }
}
