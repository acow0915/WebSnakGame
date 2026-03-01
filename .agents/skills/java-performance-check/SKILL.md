---
name: java-performance-check
description: 用於檢查與改善 Java/Spring Boot 效能（延遲、吞吐、CPU、記憶體、GC、DB、Redis、外部呼叫）。只有在「慢查詢、timeout、吞吐不足、CPU 飆高、記憶體上升、GC 過多」或需要效能驗收時使用；一般功能開發不必用。
---

# Java Performance Check Skill

## Goals
- 找出 bottleneck（CPU/IO/DB/Lock/GC/Network）
- 提供可落地的改善方案（含驗證方法）
- 降低風險：避免「猜測式優化」

## Required Inputs (ask only if missing in context)
- 影響範圍：哪個 API / job / use case
- 指標：P50/P95/P99 latency、QPS、error rate
- 環境：local/staging/prod、instance size、DB/Redis 規模
- 觀察資料：APM trace、logs、slow query、GC logs（若有）

## Workflow
1. Symptom classification
    - Latency 高？吞吐低？timeout？CPU high？memory leak？
2. Establish baseline
    - 目前的 P95/P99、QPS、CPU/mem/GC、DB 指標
3. Identify dominant bottleneck (prioritize evidence)
   A) DB/Query
   B) External calls (HTTP/gRPC)
   C) Serialization / mapping
   D) Locks / contention
   E) GC / allocation
   F) Thread pool starvation
4. Recommend fixes with verification steps
    - 每個修正都要附「怎麼量測證明有效」

## Checklists

### A) Database
- 是否有 N+1 查詢、缺 index、全表掃描
- 是否有慢查詢（Explain plan / slow query log）
- 分頁是否使用 offset 太深（改 seek pagination）
- 查詢是否只取需要欄位（避免 SELECT *）
- 交易範圍是否過大（縮小 transaction boundary）

### B) ORM / JPA
- Lazy/Eager 是否導致多次 SQL
- batch size、fetch join、entity graph 使用是否合理
- 是否不必要地把大量資料載入記憶體再處理（改成 streaming / chunk）

### C) Caching (Redis / local)
- cache hit rate 是否足夠
- key 設計是否導致 hot key
- TTL、避免 cache stampede（mutex/lock 或 request coalescing）
- 是否有序列化成本（JSON vs Kryo/MsgPack，依系統限制）

### D) Concurrency / Threading
- Tomcat/Netty thread pool 是否不足或阻塞
- 是否在 request thread 做 blocking IO（外部呼叫、檔案、DNS）
- 是否有 synchronized / lock 造成 contention
- queue backlog、rejection policy

### E) GC / Memory
- allocation rate 是否過高（大量暫時物件）
- GC pause 對 P99 的影響
- 是否有 memory leak（cache 無上限、static map、ThreadLocal 未清）
- 大物件（large arrays / buffers）是否頻繁建立

### F) Serialization / Payload
- response payload 是否過大（壓縮、裁切欄位、分頁）
- Jackson mapping 是否過度（避免不必要的轉換/中介 DTO）

### G) Observability
- 是否有 traceId / span
- 是否有 metrics（latency histogram、DB time、cache time）
- 是否能拆分「總耗時」為各階段（DB、外呼、序列化）

## Output Format (always)
1) Findings (evidence-based)
- suspected bottleneck + supporting evidence
2) Quick Wins (low risk)
- 1~5 items, each with expected impact and how to verify
3) Medium-term Fixes
- refactor / architecture changes
4) Verification Plan
- benchmark / load test approach
- metrics to compare (before/after)
5) Risk Notes
- possible regressions & rollback plan