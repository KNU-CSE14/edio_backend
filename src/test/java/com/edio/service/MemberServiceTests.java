package com.edio.service;

import com.edio.user.domain.Member;
import com.edio.user.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {

    /*
        Service 메서드가 존재하지 않기 때문에 Respository Save 테스트만 존재
     */
    @Mock
    private MemberRepository memberRepository;

    String email = "test@example.com";
    String name = "Hong gildong";
    String givenName = "gildong";
    String familyName = "Hong";
    String profileUrl = "http://example.com/profile.jpg";

    @Test
    public void saveMember_whenValidMember_savesSuccessfully() {
        // given
        Member member = Member.builder()
                .email(email)
                .name(name)
                .givenName(givenName)
                .familyName(familyName)
                .profileUrl(profileUrl)
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo(email);
        assertThat(savedMember.getName()).isEqualTo(name);
        assertThat(savedMember.getGivenName()).isEqualTo(givenName);
        assertThat(savedMember.getFamilyName()).isEqualTo(familyName);
        assertThat(savedMember.getProfileUrl()).isEqualTo(profileUrl);
    }

    @Test
    public void saveMember_whenSaveFails_throwsException() {
        // given
        Member member = Member.builder()
                .email(email)
                .name(name)
                .givenName(givenName)
                .familyName(familyName)
                .profileUrl(profileUrl)
                .build();

        when(memberRepository.save(any(Member.class))).thenThrow(new RuntimeException("Database error"));

        // when & then
        assertThatThrownBy(() -> memberRepository.save(member))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }
}
