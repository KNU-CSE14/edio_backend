package com.edio.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/*
    http://ec2-43-203-169-54.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html#/
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Edio API",
                version = "1.0",
                description = "Edio API Documents<br><br>" +
                        "토큰 발급: http://ec2-3-38-251-128.ap-northeast-2.compute.amazonaws.com:8080/oauth2/authorization/google"
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)

public class SwaggerConfig {
}
