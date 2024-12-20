package com.edio.user.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.user.model.request.MemberCreateRequest;
import com.edio.user.model.response.MemberResponse;
import com.edio.user.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Members", description = "Members 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member")
    @Operation(summary = "Member 등록", description = "Member를 등록합니다.")
    public MemberResponse createAccount(@RequestBody MemberCreateRequest memberCreateRequest) {
        return memberService.createMember(memberCreateRequest);
    }
}
