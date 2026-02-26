package com.websnakgame.service;

import com.websnakgame.model.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 每 200ms 更新一次遊戲狀態，並透過 WebSocket 廣播。
 */
@Service
public class GameLoop {
    private static final String DEFAULT_ROOM_ID = "arena";

    private final GameSessionManager sessionManager;
    private final CollisionDetector collisionDetector;
    private final GameUpdater updater;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, Long> moveAckCache = new LinkedHashMap<>();

    public GameLoop(GameSessionManager sessionManager,
                    CollisionDetector collisionDetector,
                    GameUpdater updater,
                    SimpMessagingTemplate messagingTemplate) {
        this.sessionManager = sessionManager;
        this.collisionDetector = collisionDetector;
        this.updater = updater;
        this.messagingTemplate = messagingTemplate;
        this.sessionManager.ensureSession(DEFAULT_ROOM_ID);
    }

    @Scheduled(fixedDelay = 200)
    public void tick() {
        GameSession session = sessionManager.ensureSession(DEFAULT_ROOM_ID);
        if (!session.getSnake().isAlive()) {
            return;
        }
        Snake snake = session.getSnake();
        Direction direction = snake.hasPendingDirection() ? snake.getPendingDirection() : snake.getCurrentDirection();
        if (snake.hasPendingDirection()) {
            snake.setCurrentDirection(direction);
            snake.setPendingDirection(null);
        }
        Cell nextHead = snake.getHead().add(direction);
        CollisionResult result = collisionDetector.detect(session, nextHead);
        if (result == CollisionResult.DEAD) {
            snake.die();
            session.setStatus(GameStatus.ENDED);
            messagingTemplate.convertAndSend("/topic/game/" + session.getSessionId(), createStateUpdate(session));
            HashMap<String, Object> deathPayload = new HashMap<>();
            deathPayload.put("event", "death_event");
            deathPayload.put("reason", "collision");
            deathPayload.put("score", snake.getScore());
            messagingTemplate.convertAndSend("/topic/game/" + session.getSessionId(), deathPayload);
            return;
        }
        UpdateResult updateResult = updater.update(session, nextHead);
        messagingTemplate.convertAndSend("/topic/game/" + session.getSessionId(), createStateUpdate(session));
        if (updateResult == UpdateResult.GROWTH) {
            HashMap<String, Object> growthPayload = new HashMap<>();
            growthPayload.put("event", "growth_event");
            growthPayload.put("newScore", snake.getScore());
            messagingTemplate.convertAndSend("/topic/game/" + session.getSessionId(), growthPayload);
        }
    }

    /**
     * 生成要廣播的遊戲狀態摘要。
     */
    private GameState createStateUpdate(GameSession session) {
        Snake snake = session.getSnake();
        return new GameState(session.getCurrentTick(), session.getStatus(), snake.getPlayerId(), snake.getScore(),
                snake.getCurrentDirection(), snake.snapshotBody(), session.getFood().getLocation());
    }
}
