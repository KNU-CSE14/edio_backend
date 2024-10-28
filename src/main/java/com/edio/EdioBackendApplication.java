package com.edio;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EdioBackendApplication {
	private static final Logger logger = LoggerFactory.getLogger(EdioBackendApplication.class);

	public static void main(String[] args) {
		// .env 파일 로드
		Dotenv dotenv = Dotenv.configure().directory("./").load();

		// 환경 변수 값으로 시스템 속성 설정
		System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
		System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));
		System.setProperty("spring.datasource.url", dotenv.get("DB_URL"));

		SpringApplication.run(EdioBackendApplication.class, args);

	}

	@Bean
	public CommandLineRunner logDatabaseProperties() {
		return args -> {
			logger.info("DB Username: " + System.getProperty("spring.datasource.username"));
			logger.info("DB Password: " + System.getProperty("spring.datasource.password"));
			logger.info("DB URL: " + System.getProperty("spring.datasource.url"));
		};
	}

}
