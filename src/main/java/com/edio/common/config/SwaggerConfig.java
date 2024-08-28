package com.edio.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/*
    http://localhost:8080/swagger-ui/index.html#/
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Edio API",
                version = "1.0",
                description = "Edio API Documents"
        )
// Header Token 설정
//        security = @SecurityRequirement(name = "bearerAuth")
)
//@SecurityScheme(
//        name = "bearerAuth",
//        type = SecuritySchemeType.HTTP,
//        scheme = "bearer",
//        bearerFormat = "JWT"
//)

public class SwaggerConfig {
}
