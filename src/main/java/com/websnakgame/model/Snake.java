package com.websnakgame.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 蛇的資料結構，包含身體、方向、分數與輸入時間處理。
 */
public class Snake {
    private final String playerId;
    private final Deque<Cell> body = new ArrayDeque<>();
    private Direction currentDirection;
    private Direction pendingDirection;
    private boolean alive = true;
    private int score = 0;
    private long lastInputTime = 0;

    public Snake(String playerId, Cell headStart, Direction initialDirection, int initialLength) {
        this.playerId = playerId;
        this.currentDirection = initialDirection;
        for (int i = initialLength - 1; i >= 0; i--) {
            Cell cell;
            if (initialDirection == Direction.RIGHT) {
                cell = new Cell(headStart.getX() - i, headStart.getY());
            } else if (initialDirection == Direction.LEFT) {
                cell = new Cell(headStart.getX() + i, headStart.getY());
            } else if (initialDirection == Direction.UP) {
                cell = new Cell(headStart.getX(), headStart.getY() + i);
            } else {
                cell = new Cell(headStart.getX(), headStart.getY() - i);
            }
            body.add(cell);
        }
    }

    public String getPlayerId() {
        return playerId;
    }

    /** 取得蛇身格子清單。 */
    public Deque<Cell> getBody() {
        return body;
    }

    /** 取得蛇頭位置。 */
    public Cell getHead() {
        return body.peekLast();
    }

    /** 目前方向。 */
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    /** 設定目前方向。 */
    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }

    /** 即將套用的方向。 */
    public Direction getPendingDirection() {
        return pendingDirection;
    }

    /** 設定即將套用的方向。 */
    public void setPendingDirection(Direction pendingDirection) {
        this.pendingDirection = pendingDirection;
    }

    /** 判斷是否已有待處理方向。 */
    public boolean hasPendingDirection() {
        return pendingDirection != null;
    }

    /** 判斷是否仍存活。 */
    public boolean isAlive() {
        return alive;
    }

    /** 死亡後標記為不可操作。 */
    public void die() {
        this.alive = false;
    }

    /** 取得目前分數。 */
    public int getScore() {
        return score;
    }

    /** 累積分數。 */
    public void addScore(int delta) {
        this.score += delta;
    }

    /** 吃到食物時延伸身體。 */
    public void grow(Cell nextHead) {
        body.addLast(nextHead);
    }

    /** 向前移動一格，尾端消失。 */
    public void move(Cell nextHead) {
        body.addLast(nextHead);
        body.pollFirst();
    }

    /** 判斷指定格子是否為蛇身。 */
    public boolean contains(Cell cell) {
        return body.contains(cell);
    }

    /** 取得蛇身的快照，避免外部直接存取。 */
    public List<Cell> snapshotBody() {
        return new ArrayList<>(body);
    }

    /** 取得最後一次輸入時間。 */
    public long getLastInputTime() {
        return lastInputTime;
    }

    /** 設定最後一次輸入的時間點。 */
    public void setLastInputTime(long lastInputTime) {
        this.lastInputTime = lastInputTime;
    }
}
