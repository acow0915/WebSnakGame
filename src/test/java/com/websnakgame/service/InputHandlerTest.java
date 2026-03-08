package com.websnakgame.service;

import com.websnakgame.dto.GameActionPayload;
import com.websnakgame.model.Cell;
import com.websnakgame.model.Direction;
import com.websnakgame.model.Food;
import com.websnakgame.model.GameSession;
import com.websnakgame.model.Snake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * 單元測試 InputHandler，驗證方向驗證與訊息回傳。
 */
class InputHandlerTest {
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private InputHandler inputHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inputHandler = new InputHandler(messagingTemplate);
    }

    /** 建立測試用的 GameSession，包含初始蛇與食物配置。 */
    private GameSession buildSession() {
        Snake snake = new Snake("player1", new Cell(5, 5), Direction.RIGHT, 3);
        Food food = new Food(new Cell(0, 0), 1);
        return new GameSession("arena", 20, 20, snake, food);
    }

    /**
     * 驗證合法方向會把 pending 方向更新並且觸發 move_ack 回覆。
     */
    @Test
    void validDirectionSetsPendingDirectionAndAcknowledges() {
        GameSession session = buildSession();
        GameActionPayload payload = new GameActionPayload("arena", "player1", Direction.DOWN);

        inputHandler.handleMove(payload, session);

        assertEquals(Direction.DOWN, session.getSnake().getPendingDirection());
        assertEquals("player1", session.getSnake().getPlayerId());
        verify(messagingTemplate).convertAndSendToUser("player1", "/queue/move_ack", "accepted");
        verifyNoMoreInteractions(messagingTemplate);
    }

    /**
     * 偶數分數時若輸入的方向與目前方向相反，應回傳錯誤訊息而不設定 pending 方向。
     */
    @Test
    void rejectsOppositeDirectionOnEvenScoreAndReturnsError() {
        GameSession session = buildSession();
        GameActionPayload payload = new GameActionPayload("arena", "player1", Direction.LEFT);

        inputHandler.handleMove(payload, session);

        assertNull(session.getSnake().getPendingDirection());
        verify(messagingTemplate).convertAndSendToUser("player1", "/queue/input_error", "反方向不合法");
        verifyNoMoreInteractions(messagingTemplate);
    }
}
