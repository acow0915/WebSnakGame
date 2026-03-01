---
name: spring-boot-api-design
description: 用於設計/Review Spring Boot REST API（端點、DTO、錯誤格式、驗證、版本、文件）。只有在「要新增/改動 API 合約、Controller、Request/Response DTO、OpenAPI 文件」時使用；純修內部邏輯或 UI 不要用。
---

# Spring Boot API Design Skill

## Goals
- 設計清晰、可擴展、可維護的 REST API 合約
- 降低破壞性變更風險（backward compatibility）
- 輸出一致的錯誤格式與驗證策略

## Assumptions / Defaults (unless user specifies otherwise)
- Spring Boot + Jackson
- JSON API
- 時間採用 ISO-8601（UTC 或明確 timezone）
- 以 resource 為中心設計（非 RPC）

## Workflow
1. Clarify (from context) the resource & actions
    - 資源是什麼？（User/Order/Payment…）
    - 主要 use cases？（list/get/create/update/delete）
2. Propose endpoints
    - Method + Path + Query params
    - 篩選/排序/分頁策略
3. Define request/response DTOs
    - 欄位命名、型別、nullable 規則
    - enum 與字串限制
4. Validation & errors
    - Bean Validation（@Valid, @NotNull, @Size…）
    - 統一錯誤回應格式（見下）
5. Versioning & compatibility
    - 不可破壞性原則：新增欄位 OK、移除/改型別 NG
    - 若需要破壞性變更：新版本 path 或 header
6. Security & idempotency
    - 認證授權位置（method security / filter）
    - POST/PUT/PATCH 的重送策略（Idempotency-Key 如有需要）
7. Documentation
    - 提供 OpenAPI 的端點摘要、範例 payload（至少 1 組）

## REST Conventions
- Paths 用複數名詞：/users /orders
- GET 列表：GET /orders?page=1&size=20&sort=createdAt,desc
- GET 單筆：GET /orders/{id}
- POST 建立：POST /orders -> 201 + Location header（可選）
- PUT 全量更新 / PATCH 部分更新（若差異明確）
- DELETE 刪除：204 或 200（看是否回傳 body）

## Error Response Contract (default)
Return JSON:
{
"code": "VALIDATION_ERROR|NOT_FOUND|CONFLICT|UNAUTHORIZED|FORBIDDEN|INTERNAL_ERROR",
"message": "human readable summary",
"details": [
{"field": "email", "reason": "must be a well-formed email address"}
],
"traceId": "..."
}

- traceId 來自 request correlation id（若系統有）

## Output Format (always)
1) API Contract
- Endpoints table (method, path, purpose)
- Query params / headers
2) DTOs
- Request DTO
- Response DTO
- Field notes (required/optional, constraints)
3) Validation & Error Mapping
- validation rules
- error codes + HTTP status mapping
4) Compatibility Notes
- breaking change risks + migration plan
5) OpenAPI Snippet / Examples
- sample request/response JSON