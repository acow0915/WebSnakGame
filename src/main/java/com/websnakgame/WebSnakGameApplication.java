package com.websnakgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot 應用的起始類別，負責啟動整個 WebSnakGame 服務。
 */
@SpringBootApplication
@EnableScheduling
public class WebSnakGameApplication {
    /**
     * 啟動 Spring Boot 容器，將所有 Bean 註冊並開啟 WebSocket 端點。
     */
    public static void main(String[] args) {
        SpringApplication.run(WebSnakGameApplication.class, args);
    }
}
