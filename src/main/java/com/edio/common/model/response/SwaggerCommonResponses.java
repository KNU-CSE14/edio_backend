package com.edio.common.model.response;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Swagger 관련 공통 응답 정의
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "400", description = "파라미터 오류"),
        @ApiResponse(responseCode = "401", description = "인증받지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음")
})
public @interface SwaggerCommonResponses {
}
