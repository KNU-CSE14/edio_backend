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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableJpaAuditing  // JPA Auditing 활성화
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
        if(profile == null || profile.isEmpty()){
            Dotenv dotenv = Dotenv.load();
            profile = dotenv.get("SPRING_PROFILES_ACTIVE");
        }
        System.setProperty("spring.profiles.active", profile);

        SpringApplication app = new SpringApplication(EdioBackendApplication.class);
        // local 프로파일일 때만 .env 파일의 변수들을 Spring Environment에 추가
        if("local".equals(profile)){
            app.addInitializers(context -> {
                ConfigurableEnvironment env = context.getEnvironment();
                Map<String, Object> envVars = new HashMap<>();
                Dotenv dotenv = Dotenv.load();
                dotenv.entries().forEach(entry -> envVars.put(entry.getKey(), entry.getValue()));
                env.getPropertySources().addFirst(new org.springframework.core.env.MapPropertySource("dotenvProperties", envVars));
            });
        }
        app.run(args);
    }
}
