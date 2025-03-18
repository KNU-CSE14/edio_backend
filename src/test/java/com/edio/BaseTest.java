package com.edio;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

public abstract class BaseTest {

    @BeforeAll
    public static void setUp() throws InterruptedException {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        Map<String, String> mapping = Map.ofEntries(
                Map.entry("spring.datasource.username", "DB_USERNAME"),
                Map.entry("spring.datasource.password", "DB_PASSWORD"),
                Map.entry("spring.datasource.url", "DB_URL"),
                Map.entry("google.client-id", "GOOGLE_CLIENT_ID"),
                Map.entry("google.client-secret", "GOOGLE_CLIENT_SECRET"),
                Map.entry("jwt.secret", "JWT_SECRET"),
                Map.entry("spring.profiles.active", "SPRING_PROFILES_ACTIVE"),
                Map.entry("redirect.url", "REDIRECT_URL"),
                Map.entry("AWS_REGION", "AWS_REGION"),
                Map.entry("AWS_ACCESS_KEY_ID", "AWS_ACCESS_KEY_ID"),
                Map.entry("AWS_SECRET_KEY_ID", "AWS_SECRET_KEY_ID"),
                Map.entry("AWS_BUCKET_NAME", "AWS_BUCKET_NAME")
        );

        mapping.forEach((systemKey, envKey) -> {
            String value = dotenv.get(envKey);
            if (value == null) {
                value = System.getenv(envKey);
            }
            if (value != null) {
                System.setProperty(systemKey, value);
            }
        });
    }
}
