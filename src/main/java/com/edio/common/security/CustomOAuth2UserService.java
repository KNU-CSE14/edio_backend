package com.edio.common.security;

import com.edio.user.domain.Accounts;
import com.edio.user.domain.Members;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(AccountRepository accountRepository, MemberRepository memberRepository) {
        this.accountRepository = accountRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String givenName = oAuth2User.getAttribute("given_name"); // OAuth2 공급자가 제공하는 first name
        String familyName = oAuth2User.getAttribute("family_name"); // OAuth2 공급자가 제공하는 last name
        String profileUrl = oAuth2User.getAttribute("picture"); // 프로필 사진 URL

        Accounts account = accountRepository.findByLoginIdAndStatus(email, true)
                .orElseGet(() -> {
                    // 계정 생성
                    Accounts newAccount = Accounts.builder()
                            .loginId(email)
                            .loginType(AccountLoginType.GOOGLE)
                            .roles(AccountRole.ROLE_USER)
                            .build();
                    Accounts savedAccount = accountRepository.save(newAccount);

                    // 회원 정보 생성
                    Members newMember = Members.builder()
                            .accountId(savedAccount.getId())
                            .email(email)
                            .name(name)
                            .givenName(givenName)
                            .familyName(familyName)
                            .profileUrl(profileUrl)
                            .build();
                    memberRepository.save(newMember);

                    return newAccount;
                });
        return new CustomOAuth2User(oAuth2User, account.getRoles().name());
    }
}
