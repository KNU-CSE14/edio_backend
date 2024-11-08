package com.edio.user.service;

import com.edio.common.exception.NotFoundException;
import com.edio.user.domain.Members;
import com.edio.user.model.reponse.MemberResponse;
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
    public MemberResponse createMember(Members member) {
        Members savedMember = memberRepository.findByAccountId(member.getAccountId())
                .orElseGet(() -> {
                    Members newMember = Members.builder()
                            .accountId(member.getAccountId())
                            .email(member.getEmail())
                            .name(member.getName())
                            .givenName(member.getGivenName())
                            .familyName(member.getFamilyName())
                            .profileUrl(member.getProfileUrl())
                            .build();
                    return memberRepository.save(newMember);
                });
        return MemberResponse.from(savedMember);
    }
}
