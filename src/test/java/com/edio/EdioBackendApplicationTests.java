package com.edio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mysql")
class EdioBackendApplicationTests extends BaseTest {
    @Test
    void contextLoads() {
        // This test is used to check if the Spring application context loads successfully.
    }
}
