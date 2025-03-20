package com.edio.common.security;

import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static com.edio.common.TestConstants.User.*;
import static com.edio.common.TestConstants.Folder.FOLDER_ID;
import static com.edio.common.TestConstants.Folder.FOLDER_NAME;
import static com.edio.common.util.TestUserUtil.account;
import static com.edio.common.util.TestUserUtil.member;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomOAuth2UserServiceTest {

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

    private Member mockMember = member();
    private Account mockAccount = account(mockMember);
    private Folder mockFolder;

    private OAuth2User dummyOAuth2User;
    private OAuth2UserRequest userRequest;

    @BeforeEach
    public void setUp() {
        // 테스트용 데이터 생성
        mockFolder = Folder.builder()
                .id(FOLDER_ID)
                .accountId(ACCOUNT_ID)
                .parentFolder(null)
                .name(FOLDER_NAME)
                .build();

        // 더미 OAuth2User 생성 (mock)
        dummyOAuth2User = mock(OAuth2User.class);

        // OAuth2 공급자가 제공하는 사용자 정보를 모방하기 위한 속성 맵 생성
        Map<String, Object> attributes = Map.of(
                "email", EMAIL,
                "name", NAME,
                "given_name", GIVEN_NAME,
                "family_name", FAMILY_NAME,
                "picture", PROFILE_URL
        );
        // getAttributes()가 속성 맵을 반환하도록 Mock 처리
        when(dummyOAuth2User.getAttributes()).thenReturn(attributes);
        // getAttribute()가 attributes 맵에서 값을 가져오도록 설정
        when(dummyOAuth2User.getAttribute(anyString())).thenAnswer(invocation -> attributes.get(invocation.getArgument(0)));

        userRequest = mock(OAuth2UserRequest.class);

        // DefaultOAuth2UserService 주입
        ReflectionTestUtils.setField(customOAuth2UserService, "defaultOAuth2UserService", defaultOAuth2UserService);
    }

    @Test
    @DisplayName("기존 계정이 존재할 때 loadUser -> (성공)")
    public void 기존계정이_존재할_때_loadUser_정상동작_테스트() {
        // When
        when(accountRepository.findByLoginIdAndIsDeletedFalse(EMAIL)).thenReturn(Optional.of(mockAccount));

        when(defaultOAuth2UserService.loadUser(any(OAuth2UserRequest.class))).thenReturn(dummyOAuth2User);

        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // Then: 기존 계정이 이미 존재하므로 신규 등록(save 관련 메서드 호출)이 발생하지 않아야 함
        verify(accountRepository, times(1)).findByLoginIdAndIsDeletedFalse(EMAIL);
        verify(memberRepository, never()).save(any(Member.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(folderRepository, never()).save(any(Folder.class));

        // 반환된 OAuth2User가 CustomUserDetails 타입인지, 그리고 내부에 저장된 계정 정보가 mockAccount와 일치하는지 확인
        assertInstanceOf(CustomUserDetails.class, result);
        CustomUserDetails userDetails = (CustomUserDetails) result;
        assertEquals(mockAccount.getId(), userDetails.getAccountId());
        assertEquals(mockAccount.getLoginId(), userDetails.getUsername());
    }

    @Test
    @DisplayName("신규 계정일 때 loadUser -> (성공)")
    public void 신규계정일_때_loadUser_정상동작_테스트() {
        // When
        // 신규 계정이므로 findBy...는 빈 Optional 반환
        when(accountRepository.findByLoginIdAndIsDeletedFalse(EMAIL)).thenReturn(Optional.empty());

        // 내부 save 메서드들이 호출되었을 때, 더미 객체들을 반환하도록 stub
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);
        when(folderRepository.save(any(Folder.class))).thenReturn(mockFolder);

        when(defaultOAuth2UserService.loadUser(any(OAuth2UserRequest.class))).thenReturn(dummyOAuth2User);

        // CustomOAuth2UserService의 loadUser 호출
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // Then: 신규 계정 생성과 관련한 repository의 save 메서드들이 호출되었는지 검증
        verify(accountRepository, times(1)).findByLoginIdAndIsDeletedFalse(EMAIL);
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
