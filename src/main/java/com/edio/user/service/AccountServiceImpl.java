package com.edio.user.service;

import com.edio.common.exception.ConflictException;
import com.edio.common.exception.NotFoundException;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
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
            Member member = memberRepository.findById(accountCreateRequest.getMemberId())
                    .orElseThrow(() -> new NotFoundException(Member.class, accountCreateRequest.getMemberId()));

            Account newAccount = Account.builder()
                    .loginId(accountCreateRequest.getLoginId())
                    .password(oauthPassword)
                    .member(member)
                    .loginType(AccountLoginType.GOOGLE) // 기본값을 사용하지 않고 명시적으로 설정
                    .roles(AccountRole.ROLE_USER) // 기본값을 사용하지 않고 명시적으로 설정
                    .build();
            Account savedAccount = accountRepository.save(newAccount);
            return AccountResponse.from(savedAccount);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(Account.class, accountCreateRequest.getLoginId());
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
