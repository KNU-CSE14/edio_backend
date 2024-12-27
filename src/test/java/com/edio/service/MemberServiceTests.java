package com.edio.service;

import com.edio.common.exception.custom.ConflictException;
import com.edio.user.domain.Member;
import com.edio.user.model.request.MemberCreateRequest;
import com.edio.user.model.response.MemberResponse;
import com.edio.user.repository.MemberRepository;
import com.edio.user.service.MemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    String email = "test@example.com";
    String name = "Hong gildong";
    String givenName = "gildong";
    String familyName = "Hong";
    String profileUrl = "http://example.com/profile.jpg";

    @Test
    public void createMember_whenMemberDoesNotExist_createsNewMember() {
        // given
        MemberCreateRequest member = new MemberCreateRequest(email, name, givenName, familyName, profileUrl);

        // when
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        MemberResponse response = memberService.createMember(member);

        // then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.name()).isEqualTo(name);
        assertThat(response.givenName()).isEqualTo(givenName);
        assertThat(response.familyName()).isEqualTo(familyName);
        assertThat(response.profileUrl()).isEqualTo(profileUrl);
    }

    // 멤버가 이미 존재할 때 기존 멤버를 반환
    @Test
    public void createMember_whenMemberExists_throwsConflictException() {
        // given
        String email = "test@example.com";
        MemberCreateRequest existingMember = new MemberCreateRequest(email, name, givenName, familyName, profileUrl);

        // save 호출 시 ConflictException 발생하도록 설정
        when(memberRepository.save(any(Member.class))).thenThrow(new ConflictException(Member.class, existingMember.email()));

        // when & then: ConflictException 발생을 기대함
        assertThatThrownBy(() -> memberService.createMember(existingMember))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("test@example.com"); // accountId가 포함된 메시지를 기대
    }

    // 잘못된 데이터로 요청이 들어온 경우 예외 발생
    @Test
    public void createMember_whenEmailIsNull_throwsException() {
        // given
        String email = null;
        MemberCreateRequest member = new MemberCreateRequest(email, name, givenName, familyName, profileUrl);


        // when, then
        assertThatThrownBy(() -> memberService.createMember(member))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("null");
    }

    // 저장 실패 시 예외를 처리
    @Test
    public void createMember_whenSaveFails_throwsException() {
        // given
        MemberCreateRequest member = new MemberCreateRequest(email, name, givenName, familyName, profileUrl);

        when(memberRepository.save(any(Member.class))).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> memberService.createMember(member))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }
}
