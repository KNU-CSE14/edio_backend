package com.edio.user.service;

import com.edio.common.exception.ConflictException;
import com.edio.common.exception.NotFoundException;
import com.edio.user.domain.Members;
import com.edio.user.model.request.MemberCreateRequest;
import com.edio.user.model.response.MemberResponse;
import com.edio.user.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
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
    public MemberResponse createMember(MemberCreateRequest memberCreateRequest) {
        try{
            Members newMember = Members.builder()
                    .accountId(memberCreateRequest.getAccountId())
                    .email(memberCreateRequest.getEmail())
                    .name(memberCreateRequest.getName())
                    .givenName(memberCreateRequest.getGivenName())
                    .familyName(memberCreateRequest.getFamilyName())
                    .profileUrl(memberCreateRequest.getProfileUrl())
                    .build();
            Members savedMember = memberRepository.save(newMember);
            return MemberResponse.from(savedMember);
        }catch (DataIntegrityViolationException e){
            throw new ConflictException(Members.class,  memberCreateRequest.getAccountId());
        }
    }
}
