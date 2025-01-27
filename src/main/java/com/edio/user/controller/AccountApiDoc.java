package com.edio.user.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.common.security.CustomUserDetails;
import com.edio.user.model.response.AccountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Accounts", description = "Accounts 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
public interface AccountApiDoc {
    @Operation(summary = "Account 정보 조회", description = "Account 정보를 조회합니다.")
    AccountResponse getAccount(CustomUserDetails userDetails);
}
