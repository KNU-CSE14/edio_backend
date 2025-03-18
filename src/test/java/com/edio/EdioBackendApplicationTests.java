package com.edio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.profiles.active=mysql")
class EdioBackendApplicationTests extends BaseTest {
    @Test
    void contextLoads() {
        // This test is used to check if the Spring application context loads successfully.
    }
}
