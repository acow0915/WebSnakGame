package com.websnakgame.controller;

import com.websnakgame.dto.GameActionPayload;
import com.websnakgame.service.GameSessionManager;
import com.websnakgame.service.InputHandler;
import javax.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * 控制器負責接收 WebSocket 傳入的玩家行為並委派至服務層處理。
 */
@Controller
public class GameController {
    private final InputHandler inputHandler;
    private final GameSessionManager sessionManager;

    public GameController(InputHandler inputHandler, GameSessionManager sessionManager) {
        this.inputHandler = inputHandler;
        this.sessionManager = sessionManager;
    }

    @MessageMapping("/action")
    /**
     * 處理前端傳入的動作請求，依 action 決定是否重置或傳遞給 InputHandler。
     */
    public void handleAction(@Valid GameActionPayload payload) {
        if ("restart".equalsIgnoreCase(payload.getAction())) {
            sessionManager.resetSession(payload.getRoomId());
            return;
        }
        sessionManager.ensureSession(payload.getRoomId());
        inputHandler.handleMove(payload, sessionManager.getSession(payload.getRoomId()));
    }
}
