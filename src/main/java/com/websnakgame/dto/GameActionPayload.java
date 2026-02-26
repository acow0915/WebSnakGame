package com.websnakgame.dto;

import com.websnakgame.model.Direction;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 表示前端傳來的遊戲操作封包，含房間、玩家、方向與特殊行為。
 */
public class GameActionPayload {
    /** 目標房間識別碼 */
    @NotBlank
    private String roomId;

    /** 發送操作的玩家識別碼 */
    @NotBlank
    private String playerId;

    /** 該筆操作欲設定的移動方向 */
    @NotNull
    private Direction direction;

    /** 可選的額外行為（預設為 move） */
    private String action = "move";

    /** 預設建構子供 JSON 反序列化使用。 */
    public GameActionPayload() {
    }

    public GameActionPayload(String roomId, String playerId, Direction direction) {
        this.roomId = roomId;
        this.playerId = playerId;
        this.direction = direction;
    }

    /**
     * 一次設定房間、玩家、方向與行為的建構子。
     */
    public GameActionPayload(String roomId, String playerId, Direction direction, String action) {
        this(roomId, playerId, direction);
        this.action = action;
    }

    /** 取得目標房間。 */
    public String getRoomId() {
        return roomId;
    }

    /** 設定目標房間。 */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /** 取得玩家識別碼。 */
    public String getPlayerId() {
        return playerId;
    }

    /** 設定玩家識別碼。 */
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    /** 取得方向。 */
    public Direction getDirection() {
        return direction;
    }

    /** 設定方向，用於控制蛇的移動。 */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /** 取得額外行為（例如 restart）。 */
    public String getAction() {
        return action;
    }

    /** 設定額外行為。 */
    public void setAction(String action) {
        this.action = action;
    }
}
