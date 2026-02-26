package com.websnakgame.model;

import java.util.List;

/**
 * 封裝遊戲狀態資訊，供前端定期刷新畫面。
 */
public class GameState {
    private final long tick;
    private final GameStatus status;
    private final String playerId;
    private final int score;
    private final Direction direction;
    private final List<Cell> body;
    private final Cell food;

    public GameState(long tick, GameStatus status, String playerId, int score, Direction direction, List<Cell> body, Cell food) {
        this.tick = tick;
        this.status = status;
        this.playerId = playerId;
        this.score = score;
        this.direction = direction;
        this.body = body;
        this.food = food;
    }

    /** 取得 tick 編號。 */
    public long getTick() {
        return tick;
    }

    /** 取得目前遊戲狀態。 */
    public GameStatus getStatus() {
        return status;
    }

    /** 取得玩家識別碼。 */
    public String getPlayerId() {
        return playerId;
    }

    /** 取得目前得分。 */
    public int getScore() {
        return score;
    }

    /** 取得目前方向。 */
    public Direction getDirection() {
        return direction;
    }

    /** 取得蛇身位置清單。 */
    public List<Cell> getBody() {
        return body;
    }

    /** 取得目前食物位置。 */
    public Cell getFood() {
        return food;
    }
}
