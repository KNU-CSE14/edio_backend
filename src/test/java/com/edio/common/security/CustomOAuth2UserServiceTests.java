package com.edio.common.security;

import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomOAuth2UserServiceTests {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private DefaultOAuth2UserService defaultOAuth2UserService;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    private Account mockAccount;
    private Member mockMember;
    private Folder mockFolder;

    private OAuth2User dummyOAuth2User;
    private OAuth2UserRequest userRequest;

    @BeforeEach
    public void setUp() {
        // 테스트용 데이터 생성
        mockMember = Member.builder()
                .email("test@example.com")
                .name("Test User")
                .givenName("Test")
                .familyName("User")
                .profileUrl("http://example.com/profile.jpg")
                .build();

        mockAccount = Account.builder()
                .loginId("test@example.com")
                .password("oauth_password")
                .member(mockMember)
                .loginType(AccountLoginType.GOOGLE)
                .roles(AccountRole.ROLE_USER)
                .build();
        ReflectionTestUtils.setField(mockAccount, "id", 1L);

        mockFolder = Folder.builder()
                .accountId(1L)
                .parentFolder(null)
                .name("Default")
                .build();
        ReflectionTestUtils.setField(mockFolder, "id", 1L);

        // 더미 OAuth2User 생성 (mock)
        dummyOAuth2User = mock(OAuth2User.class);

        // OAuth2 공급자가 제공하는 사용자 정보를 모방하기 위한 속성 맵 생성
        Map<String, Object> attributes = Map.of(
                "email", "test@example.com",
                "name", "Test User",
                "given_name", "Test",
                "family_name", "User",
                "picture", "http://example.com/profile.jpg"
        );
        // getAttributes()가 속성 맵을 반환하도록 Mock 처리
        when(dummyOAuth2User.getAttributes()).thenReturn(attributes);
        // getAttribute()가 attributes 맵에서 값을 가져오도록 설정
        when(dummyOAuth2User.getAttribute(anyString())).thenAnswer(invocation -> attributes.get(invocation.getArgument(0)));

        userRequest = mock(OAuth2UserRequest.class);
    }

    /**
     * 기존 계정이 존재하는 경우 테스트
     */
    @Test
    public void 기존계정이_존재할_때_loadUser_정상동작_테스트() {

        // 기존 계정이 존재한다고 가정하고, accountRepository.findByLoginIdAndIsDeleted()가 testAccount를 반환하도록 설정
        when(accountRepository.findByLoginIdAndIsDeleted(eq("test@example.com"), eq(false)))
                .thenReturn(Optional.of(mockAccount));

        // defaultOAuth2UserService loadUser 처리
        when(defaultOAuth2UserService.loadUser(any(OAuth2UserRequest.class)))
                .thenReturn(dummyOAuth2User);

        // CustomOAuth2UserService의 loadUser 호출
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // 검증: 기존 계정이 이미 존재하므로 신규 등록(save 관련 메서드 호출)이 발생하지 않아야 함
        verify(accountRepository, times(1))
                .findByLoginIdAndIsDeleted("test@example.com", false);
        verify(memberRepository, never()).save(any(Member.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(folderRepository, never()).save(any(Folder.class));

        // 반환된 OAuth2User가 CustomUserDetails 타입인지, 그리고 내부에 저장된 계정 정보가 mockAccount와 일치하는지 확인
        assertInstanceOf(CustomUserDetails.class, result);
        CustomUserDetails userDetails = (CustomUserDetails) result;
        assertEquals(mockAccount.getId(), userDetails.getAccountId());
        assertEquals(mockAccount.getLoginId(), userDetails.getUsername());
    }

    /**
     * 신규 계정이 생성되는 경우 테스트
     */
    @Test
    public void 신규계정일_때_loadUser_정상동작_테스트() {
        // 신규 계정이므로 findBy...는 빈 Optional 반환
        when(accountRepository.findByLoginIdAndIsDeleted(eq("test@example.com"), eq(false)))
                .thenReturn(Optional.empty());

        // 내부 save 메서드들이 호출되었을 때, 더미 객체들을 반환하도록 stub
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);
        when(folderRepository.save(any(Folder.class))).thenReturn(mockFolder);

        // defaultOAuth2UserService loadUser 처리
        when(defaultOAuth2UserService.loadUser(any(OAuth2UserRequest.class)))
                .thenReturn(dummyOAuth2User);

        // CustomOAuth2UserService의 loadUser 호출
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // then: 신규 계정 생성과 관련한 repository의 save 메서드들이 호출되었는지 검증
        verify(accountRepository, times(1))
                .findByLoginIdAndIsDeleted("test@example.com", false);
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(folderRepository, times(1)).save(any(Folder.class));

        // 반환된 OAuth2User가 CustomUserDetails 타입이며, 신규 계정 정보가 반영되었는지 확인
        assertInstanceOf(CustomUserDetails.class, result);
        CustomUserDetails userDetails = (CustomUserDetails) result;
        assertEquals(mockAccount.getId(), userDetails.getAccountId());
        assertEquals(mockAccount.getLoginId(), userDetails.getUsername());
    }
}
