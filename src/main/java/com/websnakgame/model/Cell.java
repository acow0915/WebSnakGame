package com.websnakgame.model;

import java.util.Objects;

/**
 * 表示遊戲格子位置，支援指令方向移動與比較。
 */
public class Cell {
    private final int x;
    private final int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * 根據方向回傳新座標，代表蛇頭下一格位置。
     */
    public Cell add(Direction direction) {
        if (direction == Direction.UP) {
            return new Cell(x, y - 1);
        } else if (direction == Direction.DOWN) {
            return new Cell(x, y + 1);
        } else if (direction == Direction.LEFT) {
            return new Cell(x - 1, y);
        } else {
            return new Cell(x + 1, y);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return x == cell.x && y == cell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
