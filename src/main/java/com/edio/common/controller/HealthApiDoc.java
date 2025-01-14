package com.edio.common.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "DB Health", description = "DB 연결 확인")
public interface HealthApiDoc {
    @Operation(summary = "DB 연결", description = "DB 연결을 확인합니다.")
    @SwaggerCommonResponses
    ResponseEntity<String> checkDatabaseConnection();
}
