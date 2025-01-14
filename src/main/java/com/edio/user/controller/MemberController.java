package com.edio.user.controller;

import com.edio.user.model.request.MemberCreateRequest;
import com.edio.user.model.response.MemberResponse;
import com.edio.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController implements MemberApiDoc {

    private final MemberService memberService;

    @PostMapping("/member")
    @Override
    public MemberResponse createAccount(@RequestBody MemberCreateRequest memberCreateRequest) {
        return memberService.createMember(memberCreateRequest);
    }
}
