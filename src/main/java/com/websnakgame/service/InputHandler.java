package com.websnakgame.service;

import com.websnakgame.dto.GameActionPayload;
import com.websnakgame.model.Direction;
import com.websnakgame.model.GameSession;
import com.websnakgame.model.Snake;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * 處理來自玩家的輸入請求，驗證方向並回應對應訊息。
 */
@Service
public class InputHandler {
    private final SimpMessagingTemplate messagingTemplate;

    public InputHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 驗證玩家輸入，若合法則記錄 pending direction 並回傳 ack。
     */
    public void handleMove(GameActionPayload payload, GameSession session) {
        Snake snake = session.getSnake();
        if (!snake.isAlive()) {
            return;
        }
        Direction direction = payload.getDirection();
        if (snake.getPendingDirection() != null && snake.getPendingDirection() == direction) {
            return;
        }
        if (snake.getScore() % 2 == 0 && isOpposite(snake.getCurrentDirection(), direction)) {
            messagingTemplate.convertAndSendToUser(snake.getPlayerId(), "/queue/input_error", "反方向不合法");
            return;
        }
        snake.setPendingDirection(direction);
        snake.setLastInputTime(System.currentTimeMillis());
        messagingTemplate.convertAndSendToUser(snake.getPlayerId(), "/queue/move_ack", "accepted");
    }

    /** 判斷兩方向是否為互相對應（相反方向）。 */
    private boolean isOpposite(Direction current, Direction next) {
        return (current == Direction.UP && next == Direction.DOWN)
                || (current == Direction.DOWN && next == Direction.UP)
                || (current == Direction.LEFT && next == Direction.RIGHT)
                || (current == Direction.RIGHT && next == Direction.LEFT);
    }
}
