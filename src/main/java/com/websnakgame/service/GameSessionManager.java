package com.websnakgame.service;

import com.websnakgame.model.Cell;
import com.websnakgame.model.Direction;
import com.websnakgame.model.Food;
import com.websnakgame.model.GameSession;
import com.websnakgame.model.Snake;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理遊戲房間，負責建立、重置與快取遊戲 Session。
 */
@Service
public class GameSessionManager {
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

    /** 取得已存在的房間。 */
    public GameSession getSession(String roomId) {
        return sessions.get(roomId);
    }

    /** 確保房間存在，若不存在則建立新的 Session。 */
    public GameSession ensureSession(String roomId) {
        return sessions.computeIfAbsent(roomId, this::createSession);
    }

    /** 重置指定房間並回傳新的 Session。 */
    public GameSession resetSession(String roomId) {
        GameSession session = createSession(roomId);
        sessions.put(roomId, session);
        return session;
    }

    /** 建立新房間，其中包含蛇與食物的初始設定。 */
    private GameSession createSession(String roomId) {
        Snake snake = new Snake("player1", new Cell(5, 5), Direction.RIGHT, 3);
        Food food = new Food(new Cell(0, 0), 1);
        return new GameSession(roomId, 20, 20, snake, food);
    }
}
