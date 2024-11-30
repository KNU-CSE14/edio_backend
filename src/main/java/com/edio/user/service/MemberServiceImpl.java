package com.edio.user.service;

import com.edio.common.exception.ConflictException;
import com.edio.user.domain.Member;
import com.edio.user.model.request.MemberCreateRequest;
import com.edio.user.model.response.MemberResponse;
import com.edio.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    /*
        Member 등록
     */
    @Override
    @Transactional
    public MemberResponse createMember(MemberCreateRequest memberCreateRequest) {
        try {
            Member newMember = Member.builder()
                    .email(memberCreateRequest.getEmail())
                    .name(memberCreateRequest.getName())
                    .givenName(memberCreateRequest.getGivenName())
                    .familyName(memberCreateRequest.getFamilyName())
                    .profileUrl(memberCreateRequest.getProfileUrl())
                    .build();
            Member savedMember = memberRepository.save(newMember);
            return MemberResponse.from(savedMember);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(Member.class, memberCreateRequest.getEmail());
        }
    }
}
