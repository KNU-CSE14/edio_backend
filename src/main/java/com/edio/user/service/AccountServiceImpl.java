package com.edio.user.service;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.model.request.AccountCreateRequest;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final String oauthPassword = "oauth_password";

    private final AccountRepository accountRepository;

    private final MemberRepository memberRepository;

    /*
        AccountId 조회
     */
    @Transactional(readOnly = true)
    @Override
    public Long getAccountIdByLoginId(String loginId) {
        return accountRepository.findByLoginIdAndIsDeleted(loginId, false)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Account.class.getSimpleName(), loginId)))
                .getId();
    }

    /*
        Account 조회(active)
     */
    @Transactional(readOnly = true)
    @Override
    public AccountResponse findOneAccount(Long accountId) {
        Account account = accountRepository.findByIdAndIsDeleted(accountId, false)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Account.class.getSimpleName(), accountId)));
        return AccountResponse.from(account);
    }

    /*
        Account 조회(active)
     */
    @Transactional(readOnly = true)
    @Override
    public Optional<AccountResponse> findOneAccountEmail(String email) {
        return accountRepository.findByLoginIdAndIsDeleted(email, false)
                .map(AccountResponse::from);
    }

    /*
        Account 등록
     */
    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest accountCreateRequest) {
        Member member = memberRepository.findById(accountCreateRequest.memberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Member.class.getSimpleName(), accountCreateRequest.memberId())));

        Account newAccount = Account.builder()
                .loginId(accountCreateRequest.loginId())
                .password(oauthPassword)
                .member(member)
                .loginType(accountCreateRequest.loginType()) // 기본값을 사용하지 않고 명시적으로 설정
                .roles(accountCreateRequest.role()) // 기본값을 사용하지 않고 명시적으로 설정
                .build();
        Account savedAccount = accountRepository.save(newAccount);
        return AccountResponse.from(savedAccount);
    }

    /*
        RootFolderId 수정
     */
    @Override
    @Transactional
    public void updateRootFolderId(Long accountId, Long rootFolderId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Account.class.getSimpleName(), accountId)));
        account.setRootFolderId(rootFolderId);
    }
}
