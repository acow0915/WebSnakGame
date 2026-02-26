# 開發規範：Spring Boot + Maven WebSocket 架構

## 1. 架構概要
本專案採用 Spring Boot 作為後端框架，使用 Maven 管理依賴與建置流程，並透過 WebSocket 實現前後端即時遊戲通訊。前端可使用與 Spring WebSocket 相容的 JavaScript 客戶端（例如 SockJS + Stomp.js 或原生 WebSocket API）與後端進行雙向訊息推送/接收。

<br />

## 2. Spring Boot 後端模組
### 2.1 WebSocket 設定模組
- 使用 `@Configuration` 搭配 `WebSocketMessageBrokerConfigurer` 來啟用 STOMP broker relay 或簡易 memory broker，包含 `registerStompEndpoints` 以定義 `/game` 等連線端點，搭配 `SetAllowedOriginPatterns("*")` 以容許來自不同來源的前端。
- `configureMessageBroker` 則用於設定 pub/sub topic（如 `/topic/game`）與 application prefix（如 `/app`），並可能結合 `SimpMessagingTemplate` 進行伺服器主動推送。

### 2.2 GameService
- 封裝遊戲邏輯的服務層，包含遊戲房間建立、玩家比對、回合管理、遊戲狀態維護等。
- 可搭配 `SimpMessagingTemplate` 發送與特定 `SimpMessageHeaderAccessor.getSessionId()` 相關的訊息、使用 `@MessageMapping` 處理前端傳來的 `GameActionPayload`。
- 需整合事務管理（`@Transactional`）以確保資料一致性，並考量多執行緒下的狀態同步問題。

### 2.3 資料模型
- 使用 DTO/VO 表達前端傳送與接收的訊息結構（例如 `GameRequest`、`GameResponse`、`PlayerStatus`），並搭配 `@Validated` 進行合法性檢查。
- Domain 模型（例如 `GameRoom`、`GameState`）可利用 `enum` 與 `@Embeddable` 精確紀錄回合進度與玩家資料。
- 必要時透過 Jackson annotations（`@JsonProperty`、`@JsonIgnoreProperties`）控制 WebSocket Payload 的序列化行為。

## 3. Maven 依賴與建置流程
1. 以 `spring-boot-starter-websocket` 與 `spring-boot-starter-web` 作為 WebSocket 與 HTTP 基礎。
2. 若需使用 STOMP，可加入 `spring-messaging` 與 `spring-boot-starter-validation`，如需儲存可再加入 `spring-boot-starter-data-jpa` 與對應資料庫驅動。
3. 使用 `mvn clean package` 或 `mvn spring-boot:run` 建置/啟動後端，確保 `pom.xml` 配置 `spring-boot-maven-plugin` 以產生可部署的 executable jar。
4. 可透過 Maven profile（例如 `dev`、`prod`）區分本地 debug 與上線運行環境的設定。

## 4. 前後端 WebSocket 通訊協定
1. 前端於遊戲頁面初始化 WebSocket，對應後端 `@Controller` 中 `@MessageMapping("/action")` 路徑進行連線。
2. 客戶端需依約定格式送出 JSON，例如 `{ "type": "move", "payload": { "playerId": "p1", "direction": "up" } }`，後端可透過 `type` 決定要呼叫的 `GameService` 方法。
3. 後端處理完後，透過 `SimpMessagingTemplate.convertAndSendToUser` 或 `convertAndSend` 廣播，例如 `/topic/game/{roomId}`，消息內容包含 `status`、`players`、`frame` 等狀態。
4. 必要時可設計 `header`（如 `simpSessionId`）以對應使用者連線，並搭配心跳/ping 機制偵測離線。

### 4.1 前端鍵盤控制規範
- 前端頁面在初次載入時即可註冊 `keydown` 事件監聽，集中於最上層的 `window` 或遊戲畫布元素，確保使用者透過鍵盤操作即可傳送指令給後端。
- 於事件中先 `preventDefault()` 以避免 `Arrow` 鍵或 `Space` 等鍵造成預設捲動或瀏覽器反應，並使用 `stopPropagation()` 來避免干擾其他 UI 控制。
- 只接受上下左右（例如 `ArrowUp`、`ArrowDown`、`ArrowLeft`、`ArrowRight`）與可選的 `W/A/S/D` 鍵，並在收到輸入前記錄目前蛇的移動方向；當新方向與目前方向為完全相反（例如目前為左移，輸入右鍵）則忽略該事件以防止瞬間自撞。
- 將合格的方向變化轉換為符合後端協定的 `GameCommand` 物件格式，例如 `{ "type": "move", "payload": { "playerId": "p1", "direction": "up" } }`，並透過 WebSocket 封包發送至 `/action` endpoint 或指定房間頻道，必要時可附加 `timestamp` 或 `frameId` 輔助伺服器同步。
- 為了維持輸入一致性，可於接收到 `keydown` 後暫時禁用該按鍵直到伺服器回應或下一個合法方向進入，並搭配動畫/圖示顯示當前方向或錯誤輸入，提升使用者操作回饋。保持這套事件處理邏輯集中在前端控制模組，方便日後透過測試模擬鍵盤事件。

### 4.2 貪食蛇遊戲規則
- 玩家使用鍵盤方向鍵（或對應的 `W/A/S/D`）控制方向，蛇會持續自動前進，前端需把每次方向變更封裝成 `move` 指令送至後端。蛇的當前方向與前端狀態同步以維持畫面與邏輯一致。
- 每次前端方向切換或定期 Tick 由後端驅動遊戲更新，後端會先驗證該方向是否有效（不能直接走回頭）並更新蛇頭位置，再決定是否與食物、牆壁或自身相撞。
- 當蛇的頭部與食物重疊時視為吃到食物，後端需依照規則增加蛇身長度、更新食物位置與分數，並在回傳的遊戲狀態中標記這次更新（例如 `event: "growth"`），以便前端顯示加長效果與音效。
- 若蛇與牆壁或自己的身體相撞，後端要將該玩家狀態標記為 `dead`/`failed`，更新 `GameState` 中的 `isAlive` 或 `status`，並附帶原因（例如 `reason: "collision"`）以方便分析。
- 死亡/失敗訊息應由後端透過 `GameResponse` 類型推送給相關房間或特定玩家（`/topic/game/{roomId}` 或 `/user/{sessionId}`），前端收到後應立即停止移動輸入、呈現死亡動畫並顯示提示文字。同時後端也可提供重新開始或觀戰的選項。
- 後端驗證成功後才會將新的遊戲狀態發送給前端；若驗證失敗（例如方向非法）則應回傳錯誤狀態而不是直接更新，以維持一致性並可選擇在前端顯示提示。需保留額外的 logging/metrics，以便追蹤常見的失敗原因。

## 5. 架構適用性評估與建議
### 優點
- Spring Boot + Maven 提供成熟的依賴管理與建置能力，快速導入 WebSocket 支持。
- WebSocket 適合同步性高的遊戲場景，可打造雙向即時通訊。
- 模組分工明確，Service 層、資料模型、WebSocket 設定分離，有利單元測試與維護。

### 缺點
- WebSocket 必須處理連線管理、狀態一致性與例外重試，比 HTTP REST 複雜。
- 若遊戲流量提升，需搭配 Redis 等訊息中介或 Session 共享來支援多節點。
- Maven 打包後的 jar 若沒有整合外部設定，部署時需注意設定檔變更與環境變數同步。

### 部署考量與建議
- 建議使用 Container（如 Docker）搭配 `ENTRYPOINT ["java", "-jar", "/app/app.jar"]` 與 `COPY application-${spring.profiles.active}.yml /config/` 等方式封裝環境。
- 透過 Maven `spring-boot-maven-plugin` 建立 fat jar，並將 `application-dev.yml`、`application-prod.yml` 等配置放在版本控制以利 CI/CD pipeline 引用。
- 部署時應搭配反向代理（如 Nginx）負責 TLS 終端與 WebSocket proxy，並設定 `proxy_set_header Upgrade $http_upgrade; proxy_set_header Connection \"upgrade\";` 以維持連線。
- 建議導入 Prometheus + Grafana 監控連線數、延遲與錯誤率，並使用 Redis/Hazelcast 等共享儲存來同步多人遊戲的 session/room 資訊。

## 6. 註解風格與維護規範
- 所有 Java 原始碼需以繁體中文撰寫註解，包含類別、方法、欄位與重要邏輯判斷，說明此段程式的用途與行為（必要時可附上英文關鍵字）。
- 儘可能將註解定位在相關程式區塊的上方或行內，保持簡潔（1-2 句話），並避免重複描述程式語法本身。
- 對於複雜演算法、多執行緒或邏輯分支，應說明前置假設與預期結果，並紀錄任何特別的錯誤處理或恢復流程。
