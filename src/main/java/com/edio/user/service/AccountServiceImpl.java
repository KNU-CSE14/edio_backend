package com.edio.user.service;

import com.edio.common.exception.custom.ConflictException;
import com.edio.common.exception.custom.NotFoundException;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.model.request.AccountCreateRequest;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new NotFoundException(Account.class, loginId))
                .getId();
    }

    /*
        Account 조회(active)
     */
    @Transactional(readOnly = true)
    @Override
    public AccountResponse findOneAccount(Long accountId) {
        Account account = accountRepository.findByIdAndIsDeleted(accountId, false)
                .orElseThrow(() -> new NotFoundException(Account.class, accountId));
        return AccountResponse.from(account);
    }

    /*
        Account 조회(active)
     */
    @Transactional(readOnly = true)
    @Override
    public AccountResponse findOneAccountEmail(String email) {
        Account account = accountRepository.findByLoginIdAndIsDeleted(email, false)
                .orElseThrow(() -> new NotFoundException(Account.class, email));
        return AccountResponse.from(account);
    }

    /*
        Account 등록
     */
    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest accountCreateRequest) {
        try {
            Member member = memberRepository.findById(accountCreateRequest.memberId())
                    .orElseThrow(() -> new NotFoundException(Member.class, accountCreateRequest.memberId()));

            Account newAccount = Account.builder()
                    .loginId(accountCreateRequest.loginId())
                    .password(oauthPassword)
                    .member(member)
                    .loginType(accountCreateRequest.loginType()) // 기본값을 사용하지 않고 명시적으로 설정
                    .roles(accountCreateRequest.role()) // 기본값을 사용하지 않고 명시적으로 설정
                    .build();
            Account savedAccount = accountRepository.save(newAccount);
            return AccountResponse.from(savedAccount);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(Account.class, accountCreateRequest.loginId());
        }
    }

    /*
        RootFolderId 수정
     */
    @Override
    @Transactional
    public void updateRootFolderId(Long accountId, Long rootFolderId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(Account.class, accountId));
        account.setRootFolderId(rootFolderId);
    }
}
