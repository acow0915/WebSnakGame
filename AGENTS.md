## Code style
- 所有註解必須以繁體中文書寫（參照 [`development-guidelines.md`](development-guidelines.md:68-70)）。
- 所有 Java 方法名稱請使用英文駝峰式命名，並附上詳細的中文註解描述其行為與情境。
- `GameActionPayload.action` 內建為 "move"，若要觸發 `restart` 等額外行為，務必在呼叫 `/action` 前顯式設值（詳見 [`src/main/java/com/websnakgame/dto/GameActionPayload.java`](src/main/java/com/websnakgame/dto/GameActionPayload.java:23-42)）。

## Non-obvious patterns & gotchas
- `GameLoop` 使用 `fixedDelay=200ms` 的排程，且只操作 `"arena"` 這個預設 room；每 tick 會重新載入相同 room 並透過 `/topic/game/{roomId}` 連續推送 `growth_event`/`death_event` 以及 `GameState` 快照（參見 [`src/main/java/com/websnakgame/service/GameLoop.java`](src/main/java/com/websnakgame/service/GameLoop.java:17-69)）。
- `InputHandler` 會忽略重複 pending direction，且當蛇的分數為偶數時會拒絕反方向輸入，透過 `/queue/input_error` 回傳，其他合法指令都會設定 pending 並回 `/queue/move_ack`（詳見 [`src/main/java/com/websnakgame/service/InputHandler.java`](src/main/java/com/websnakgame/service/InputHandler.java:24-40)）。
- `GameSessionManager` 每個 room 都會初始化為 20×20 網格、長度 3、起點 (5,5) 向右的 `player1` 蛇；`resetSession` 是直接重建同樣狀態（參見 [`src/main/java/com/websnakgame/service/GameSessionManager.java`](src/main/java/com/websnakgame/service/GameSessionManager.java:37-42)）。
- `GameSession.spawnFood()` 會持續重試，直到新的食物座標不與蛇身重疊，因此任何外部直接改變 `Food` 的 code 必須保持同樣的集合檢查以免造成 deadlock（參見 [`src/main/java/com/websnakgame/model/GameSession.java`](src/main/java/com/websnakgame/model/GameSession.java:68-75)）。
