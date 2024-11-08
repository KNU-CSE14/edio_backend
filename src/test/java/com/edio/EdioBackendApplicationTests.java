package com.edio;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EdioBackendApplicationTests {
	@BeforeAll
	static void setUp() {
		// .env 파일 로드
		Dotenv dotenv = Dotenv.configure().load();

		// 환경 변수 값으로 시스템 속성 설정
		System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
		System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));
		System.setProperty("spring.datasource.url", dotenv.get("DB_URL"));

		System.setProperty("google.client-id", dotenv.get("GOOGLE_CLIENT_ID"));
		System.setProperty("google.client-secret", dotenv.get("GOOGLE_CLIENT_SECRET"));

		System.setProperty("jwt.secret", dotenv.get("JWT_SECRET"));
	}

	@Test
	void contextLoads() {
	}

}
