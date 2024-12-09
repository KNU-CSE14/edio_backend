package com.edio.common.security;

import com.edio.common.exception.ConflictException;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.service.FolderService;
import com.edio.user.model.request.AccountCreateRequest;
import com.edio.user.model.request.MemberCreateRequest;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.model.response.MemberResponse;
import com.edio.user.service.AccountService;
import com.edio.user.service.MemberService;
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

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AccountService accountService;

    private final MemberService memberService;

    private final FolderService folderService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String givenName = oAuth2User.getAttribute("given_name"); // OAuth2 공급자가 제공하는 first name
        String familyName = oAuth2User.getAttribute("family_name"); // OAuth2 공급자가 제공하는 last name
        String profileUrl = oAuth2User.getAttribute("picture"); // 프로필 사진 URL

        AccountResponse accountResponse;
        try {
            // Member 생성
            MemberCreateRequest memberCreateRequest = new MemberCreateRequest(email, name, givenName, familyName, profileUrl);
            MemberResponse memberResponse = memberService.createMember(memberCreateRequest);

            // Account 생성
            AccountCreateRequest accountCreateRequest = new AccountCreateRequest(email, memberResponse.id());
            accountResponse = accountService.createAccount(accountCreateRequest);

            // RootFolder 생성
            FolderCreateRequest rootFolderRequest = new FolderCreateRequest(
                    null,
                    "Default"
            );
            FolderResponse rootFolderResponse = folderService.createFolder(accountResponse.id(), rootFolderRequest);
            accountService.updateRootFolderId(accountResponse.id(), rootFolderResponse.id());
        } catch (ConflictException e) {
            accountResponse = accountService.findOneAccountEmail(email);
        }

        // 권한 정보 설정
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(accountResponse.roles().name())
        );

        log.info("accountRes" + accountResponse.id());
        log.info("accountResId" + accountResponse.loginId());

        return new CustomUserDetails(
                accountResponse.id(),
                accountResponse.loginId(),
                authorities,
                oAuth2User.getAttributes() // OAuth2 사용자 속성 전달
        );
    }
}
