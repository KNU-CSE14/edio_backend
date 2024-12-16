package com.edio.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/*
    https://ec2-3-38-251-128.ap-northeast-2.compute.amazonaws.com/swagger-ui/index.html#/
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Edio API",
                version = "1.0",
                description = "Edio API Documents<br><br>" +
                        "토큰 발급: https://ec2-3-38-251-128.ap-northeast-2.compute.amazonaws.com/oauth2/authorization/google"
        ),
        security = @SecurityRequirement(name = "bearerAuth"),
        servers = {
//                @Server(url = "https://ec2-3-38-251-128.ap-northeast-2.compute.amazonaws.com",
                @Server(url = "http://localhost:8080",
                        description = "서버 URL"),
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)

public class SwaggerConfig {
}
