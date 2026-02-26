package com.websnakgame.model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 代表某一個遊戲房間的狀態，包括蛇、食物與格子尺寸。
 */
public class GameSession {
    private final String sessionId;
    private GameStatus status = GameStatus.RUNNING;
    private long currentTick = 0;
    private final int gridWidth;
    private final int gridHeight;
    private final Snake snake;
    private final Food food;
    private final Random random = new Random();

    public GameSession(String sessionId, int gridWidth, int gridHeight, Snake snake, Food food) {
        this.sessionId = sessionId;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.snake = snake;
        this.food = food;
        spawnFood();
    }

    public String getSessionId() {
        return sessionId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public void incrementTick() {
        currentTick++;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public Snake getSnake() {
        return snake;
    }

    public Food getFood() {
        return food;
    }

    /**
     * 重新生成食物位置，避免落在蛇身上。
     */
    public void spawnFood() {
        Set<Cell> occupied = new HashSet<>(snake.getBody());
        Cell candidate;
        do {
            candidate = new Cell(random.nextInt(gridWidth), random.nextInt(gridHeight));
        } while (occupied.contains(candidate));
        food.setLocation(candidate);
    }
}
