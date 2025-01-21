package com.edio.common.security;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    private final AccountRepository accountRepository;

    private final FolderRepository folderRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String givenName = oAuth2User.getAttribute("given_name"); // OAuth2 공급자가 제공하는 first name
        String familyName = oAuth2User.getAttribute("family_name"); // OAuth2 공급자가 제공하는 last name
        String profileUrl = oAuth2User.getAttribute("picture"); // 프로필 사진 URL

        // 계정 조회
        Optional<Account> existingAccount = accountRepository.findByLoginIdAndIsDeleted(email, false);

        // 계정 생성
        Account account = existingAccount.orElseGet(() -> createAccount(email, name, givenName, familyName, profileUrl));

        // 권한 정보 설정
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(account.getRoles().name())
        );

        return new CustomUserDetails(
                account.getId(),
                account.getRootFolderId(),
                account.getLoginId(),
                authorities,
                oAuth2User.getAttributes() // OAuth2 사용자 속성 전달
        );
    }

    @Transactional
    public Account createAccount(String email, String name, String givenName, String familyName, String profileUrl) {
        try {
            // Member 생성 및 저장
            Member newMember = Member.builder()
                    .email(email)
                    .name(name)
                    .givenName(givenName)
                    .familyName(familyName)
                    .profileUrl(profileUrl)
                    .build();
            newMember = memberRepository.save(newMember);

            // FIXME: OAuth 로그인 추가되면 동적으로 loginType, Role 생성으로 수정 필요
            AccountLoginType loginType = AccountLoginType.GOOGLE;
            AccountRole role = AccountRole.ROLE_USER;

            // Account 생성
            Account newAccount = Account.builder()
                    .loginId(email)
                    .password("oauth_password")
                    .member(newMember)
                    .loginType(loginType)
                    .roles(role)
                    .build();
            newAccount = accountRepository.save(newAccount);  // 여기서 ID가 생성됨

            // RootFolder 생성 및 Account에 할당
            Folder rootFolder = Folder.builder()
                    .accountId(newAccount.getId())
                    .parentFolder(null)
                    .name("Default")
                    .build();
            rootFolder = folderRepository.save(rootFolder);

            newAccount.setRootFolderId(rootFolder.getId());
            accountRepository.save(newAccount);

            return newAccount;
        } catch (Exception e) {
            log.error("Error occurred during OAuth2 registration: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException(ErrorMessages.GENERAL_CREATION_FAILED.getMessage());
        }
    }
}
