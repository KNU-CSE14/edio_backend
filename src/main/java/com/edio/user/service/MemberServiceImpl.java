package com.edio.user.service;

import com.edio.common.exception.NotFoundException;
import com.edio.user.domain.Members;
import com.edio.user.model.request.MemberRequest;
import com.edio.user.model.response.MemberResponse;
import com.edio.user.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /*
        Member 조회
     */
    @Transactional(readOnly = true)
    @Override
    public MemberResponse findOneMember(long accountId) {
        Members member = memberRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException(Members.class, accountId));
        return MemberResponse.from(member);
    }

    /*
        Member 등록
     */
    @Override
    @Transactional
    public MemberResponse createMember(MemberRequest memberRequest) {
        Members savedMember = memberRepository.findByAccountId(memberRequest.getAccountId())
                .orElseGet(() -> {
                    Members newMember = Members.builder()
                            .accountId(memberRequest.getAccountId())
                            .email(memberRequest.getEmail())
                            .name(memberRequest.getName())
                            .givenName(memberRequest.getGivenName())
                            .familyName(memberRequest.getFamilyName())
                            .profileUrl(memberRequest.getProfileUrl())
                            .build();
                    return memberRepository.save(newMember);
                });
        return MemberResponse.from(savedMember);
    }
}
