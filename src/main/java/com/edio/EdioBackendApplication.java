package com.edio;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ConfigurationPropertiesScan
@SpringBootApplication
public class EdioBackendApplication {
    public static void main(String[] args) {
        // .env 파일을 항상 로드
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 시스템 환경변수가 우선이며, 없을 경우 .env에서 읽음
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");
        if (profile == null || profile.isEmpty()) {
            profile = dotenv.get("SPRING_PROFILES_ACTIVE");
        }

        SpringApplication app = new SpringApplication(EdioBackendApplication.class);

        // 항상 dotenv 값을 환경 변수로 추가
        app.addInitializers(context -> {
            ConfigurableEnvironment env = context.getEnvironment();
            Map<String, Object> envVars = new HashMap<>();
            dotenv.entries().forEach(entry -> envVars.put(entry.getKey(), entry.getValue()));
            env.getPropertySources().addFirst(new org.springframework.core.env.MapPropertySource("dotenvProperties", envVars));
        });

        System.setProperty("spring.profiles.active", profile);

        app.run(args);
    }
}
