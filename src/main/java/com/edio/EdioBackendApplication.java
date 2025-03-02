package com.edio;

import com.edio.common.properties.AwsProperties;
import com.edio.common.properties.JwtProperties;
import com.edio.common.properties.RedirectProperties;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableConfigurationProperties({
        RedirectProperties.class,
        JwtProperties.class,
        AwsProperties.class
})
@SpringBootApplication
public class EdioBackendApplication {
    public static void main(String[] args) {
        // 운영환경에서는 시스템 환경변수를 우선 사용하고, 로컬인 경우에만 .env 파일 로드
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");

        SpringApplication app = new SpringApplication(EdioBackendApplication.class);

        if (profile == null || profile.isEmpty()) {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            profile = dotenv.get("SPRING_PROFILES_ACTIVE");

            app.addInitializers(context -> {
                ConfigurableEnvironment env = context.getEnvironment();
                Map<String, Object> envVars = new HashMap<>();
                dotenv.entries().forEach(entry -> envVars.put(entry.getKey(), entry.getValue()));
                env.getPropertySources().addFirst(new org.springframework.core.env.MapPropertySource("dotenvProperties", envVars));
            });
        }

        System.setProperty("spring.profiles.active", profile);

        app.run(args);
    }
}
