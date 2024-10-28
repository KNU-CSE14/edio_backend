package com.edio.common.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Tag(name = "DB Health", description = "DB 연결 확인")
@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health/db")
    @Operation(summary = "DB 연결", description = "DB 연결을 확인합니다.")
    @SwaggerCommonResponses //Swagger 공통 응답 어노테이션
    public ResponseEntity<String> checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                return ResponseEntity.ok("Database connection is OK");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection is not established");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to connect to database: " + e.getMessage());
        }
    }

}
