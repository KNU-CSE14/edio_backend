package com.edio.service;

import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OAuth2UserServiceTests {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FolderRepository folderRepository;

    private Account testAccount;
    private Member testMember;
    private Folder testFolder;

    @BeforeEach
    public void setUp() {
        // 테스트용 데이터 생성
        testMember = Member.builder()
                .email("test@example.com")
                .name("Test User")
                .givenName("Test")
                .familyName("User")
                .profileUrl("http://example.com/profile.jpg")
                .build();

        testAccount = Account.builder()
                .loginId("test@example.com")
                .password("oauth_password")
                .member(testMember)
                .loginType(AccountLoginType.GOOGLE)
                .roles(AccountRole.ROLE_USER)
                .build();

        testFolder = Folder.builder()
                .accountId(1L)
                .parentFolder(null)
                .name("Default")
                .build();
    }
}
