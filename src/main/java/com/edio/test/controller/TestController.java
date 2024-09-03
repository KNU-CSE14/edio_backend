package com.edio.test.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test", description = "Swagger 테스트")
@RestController
@RequestMapping("/")
public class TestController {

    @GetMapping("/get/{id}")
    @Operation(summary = "ID 조회", description = "Path ID를 조회합니다.")
//  Header Token 설정
//  security = @SecurityRequirement(name = "bearerAuth"))
    @SwaggerCommonResponses //Swagger 공통 응답 어노테이션
    public Long test(@Parameter(required = true, description = "path ID") @PathVariable long id){
        return id;
    }
}
