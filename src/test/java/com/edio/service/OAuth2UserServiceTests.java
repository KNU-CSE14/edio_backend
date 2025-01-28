package com.edio.service;

import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
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
}
