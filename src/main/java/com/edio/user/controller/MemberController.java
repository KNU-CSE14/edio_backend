package com.edio.user.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.user.domain.Accounts;
import com.edio.user.domain.Members;
import com.edio.user.model.reponse.AccountResponse;
import com.edio.user.model.reponse.MemberResponse;
import com.edio.user.service.AccountService;
import com.edio.user.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Members", description = "Members 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/member")
    @Operation(summary = "Member 정보 조회", description = "Member 정보를 조회합니다.")
    public MemberResponse getMember(@Parameter(required = true, description = "사용자 아이디") long accountId){
        return memberService.findOneMember(accountId);
    }

    @PostMapping("/member")
    @Operation(summary = "Member 등록", description = "Member를 등록합니다.")
    public MemberResponse createAccount(@RequestBody Members member){
        return memberService.createMember(member);
    }
}
