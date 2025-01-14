package com.edio.user.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.user.model.request.MemberCreateRequest;
import com.edio.user.model.response.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Members", description = "Members 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
public interface MemberApiDoc {
    @Operation(summary = "Member 등록", description = "Member를 등록합니다.")
    MemberResponse createAccount(MemberCreateRequest memberCreateRequest);
}
