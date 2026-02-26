package com.websnakgame.service;

import com.websnakgame.model.Cell;
import com.websnakgame.model.GameSession;
import com.websnakgame.model.Snake;
import org.springframework.stereotype.Service;

/**
 * 負責偵測蛇頭是否撞牆或撞到自己，決定遊戲狀態。
 */
@Service
public class CollisionDetector {
    public CollisionResult detect(GameSession session, Cell nextHead) {
        Snake snake = session.getSnake();
        if (nextHead.getX() < 0 || nextHead.getX() >= session.getGridWidth()
                || nextHead.getY() < 0 || nextHead.getY() >= session.getGridHeight()) {
            return CollisionResult.DEAD;
        }
        if (snake.contains(nextHead)) {
            return CollisionResult.DEAD;
        }
        return CollisionResult.SURVIVE;
    }
}
