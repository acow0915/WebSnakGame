package com.websnakgame.service;

import com.websnakgame.model.Cell;
import com.websnakgame.model.Food;
import com.websnakgame.model.GameSession;
import com.websnakgame.model.Snake;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * 處理蛇移動與吃食物的邏輯，並回傳更新結果。
 */
@Service
public class GameUpdater {
    public UpdateResult update(GameSession session, Cell nextHead) {
        Snake snake = session.getSnake();
        Food food = session.getFood();
        if (nextHead.equals(food.getLocation())) {
            snake.grow(nextHead);
            snake.addScore(food.getValue());
            session.spawnFood();
            return UpdateResult.GROWTH;
        }
        snake.move(nextHead);
        return UpdateResult.MOVE_ONLY;
    }
}
