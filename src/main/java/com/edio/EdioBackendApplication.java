package com.edio;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Slf4j
@EnableJpaAuditing  // JPA Auditing 활성화
@SpringBootApplication
public class EdioBackendApplication {

    public static void main(String[] args) {
        // .env 파일 로드
        Dotenv dotenv = Dotenv.load();

        // 환경 변수 값으로 시스템 속성 설정
        System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
        System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));
        System.setProperty("spring.datasource.url", dotenv.get("DB_URL"));

        System.setProperty("google.client-id", dotenv.get("GOOGLE_CLIENT_ID"));
        System.setProperty("google.client-secret", dotenv.get("GOOGLE_CLIENT_SECRET"));

        System.setProperty("jwt.secret", dotenv.get("JWT_SECRET"));

        System.setProperty("spring.profiles.active", dotenv.get("SPRING_PROFILES_ACTIVE"));

        // 로그 출력
        log.info("DB_USERNAME: {}", dotenv.get("DB_USERNAME"));
        log.info("DB_PASSWORD: {}", dotenv.get("DB_PASSWORD")); // 민감한 정보 출력 시 주의
        log.info("DB_URL: {}", dotenv.get("DB_URL"));
        log.info("GOOGLE_CLIENT_ID: {}", dotenv.get("GOOGLE_CLIENT_ID"));
        log.info("GOOGLE_CLIENT_SECRET: {}", dotenv.get("GOOGLE_CLIENT_SECRET")); // 민감한 정보 출력 시 주의
        log.info("JWT_SECRET: {}", dotenv.get("JWT_SECRET")); // 민감한 정보 출력 시 주의
        log.info("SPRING_PROFILES_ACTIVE: {}", dotenv.get("SPRING_PROFILES_ACTIVE"));

        SpringApplication.run(EdioBackendApplication.class, args);
    }
}
