package com.edio.user.service;

import com.edio.user.domain.Accounts;
import com.edio.user.domain.Members;
import com.edio.user.model.reponse.AccountResponse;
import com.edio.user.model.reponse.MemberResponse;

import java.util.Optional;

public interface MemberService {
    // Member 조회
    MemberResponse findOneMember(long accountId);
    // Member 등록
    MemberResponse createMember(Members member);
}
