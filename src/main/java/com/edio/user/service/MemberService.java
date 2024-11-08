package com.edio.user.service;

import com.edio.user.domain.Members;
import com.edio.user.model.request.MemberRequest;
import com.edio.user.model.response.MemberResponse;

public interface MemberService {
    // Member 조회
    MemberResponse findOneMember(long accountId);
    // Member 등록
    MemberResponse createMember(MemberRequest memberRequest);
}
