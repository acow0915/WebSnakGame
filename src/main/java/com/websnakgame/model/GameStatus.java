package com.websnakgame.model;

/**
 * 表示當前遊戲的狀態流程。 
 */
public enum GameStatus {
    /** 等待開始 */
    WAITING,
    /** 遊戲進行中 */
    RUNNING,
    /** 遊戲結束 */
    ENDED
}
