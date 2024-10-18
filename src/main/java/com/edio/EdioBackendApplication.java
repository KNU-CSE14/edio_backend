package com.edio;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EdioBackendApplication {

	public static void main(String[] args) {

		// .env 파일 로드
		Dotenv dotenv = Dotenv.configure().load();

		// 환경 변수 값으로 시스템 속성 설정
		System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
		System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));
		System.setProperty("spring.datasource.url", dotenv.get("DB_URL"));

		SpringApplication.run(EdioBackendApplication.class, args);

	}

}
