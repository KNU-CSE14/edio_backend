package com.edio.user.service;

import com.edio.user.model.request.MemberCreateRequest;
import com.edio.user.model.response.MemberResponse;

public interface MemberService {
    // Member 등록
    MemberResponse createMember(MemberCreateRequest memberCreateRequest);
}
