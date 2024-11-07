package com.edio.service;

import com.edio.user.domain.Members;
import com.edio.user.model.request.MemberRequest;
import com.edio.user.model.response.MemberResponse;
import com.edio.user.repository.MemberRepository;
import com.edio.user.service.MemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    @Test
    public void createMember_whenMemberDoesNotExist_createsNewMember() {
        // given
        String email = "test@example.com";
        String name = "Hong gildong";
        String givenName = "gildong";
        String familyName = "Hong";
        String profileUrl = "http://example.com/profile.jpg";

        MemberRequest member = new MemberRequest();
        member.setAccountId(1L);
        member.setEmail(email);
        member.setName(name);
        member.setGivenName(givenName);
        member.setFamilyName(familyName);
        member.setProfileUrl(profileUrl);

        // when
        when(memberRepository.findByAccountId(1L)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Members.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        MemberResponse response = memberService.createMember(member);

        // then
        assertThat(response).isNotNull();
        assertThat(response.accountId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.name()).isEqualTo(name);
        assertThat(response.givenName()).isEqualTo(givenName);
        assertThat(response.familyName()).isEqualTo(familyName);
        assertThat(response.profileUrl()).isEqualTo(profileUrl);
    }

    // 멤버가 이미 존재할 때 기존 멤버를 반환
    @Test
    public void createMember_whenMemberExists_returnsExistingMember() {
        // given
        MemberRequest existingMember = new MemberRequest();
        existingMember.setAccountId(1L);
        existingMember.setEmail("test@example.com");

        //entity
        Members existingMemberEntity = Members.builder()
                .accountId(1L)
                .email("test@example.com")
                .name("Hong gildong")
                .build();

        when(memberRepository.findByAccountId(1L)).thenReturn(Optional.of(existingMemberEntity));

        // when
        MemberResponse response = memberService.createMember(existingMember);

        // then
        assertThat(response).isNotNull();
        assertThat(response.accountId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.name()).isEqualTo("Hong gildong");
    }

    // 잘못된 데이터로 요청이 들어온 경우 예외 발생
    @Test
    public void createMember_whenEmailIsNull_throwsException() {
        // given
        MemberRequest member = new MemberRequest();
        member.setAccountId(1L);
        member.setEmail(null);

        // when, then
        assertThatThrownBy(() -> memberService.createMember(member))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("null");
    }

    // 저장 실패 시 예외를 처리
    @Test
    public void createMember_whenSaveFails_throwsException() {
        // given
        MemberRequest member = new MemberRequest();
        member.setAccountId(1L);
        member.setEmail("test@example.com");

        when(memberRepository.save(any(Members.class))).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> memberService.createMember(member))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }
}
