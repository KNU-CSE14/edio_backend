package com.edio.user;

import com.edio.common.BaseTest;
import com.edio.user.domain.Accounts;
import com.edio.user.domain.Members;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.validation.Validator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTests extends BaseTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private Validator validator;

    @BeforeEach
    void clearData() {
        accountRepository.deleteAll();
        memberRepository.deleteAll();
    }

    /*
       사용자(계정, 정보) 생성
    */
    @Test
    void testCreateAccountAndMember() throws Exception {
        Accounts newAccount = new Accounts();
        newAccount.setLoginId("test123@google.com");
        newAccount.setPassword(null);
        newAccount.setDeletedAt(null);
        newAccount.setStatus("active");
        newAccount.setLoginType("google");
        newAccount.setRoles("ROLE_USER");
        Accounts savedAccount = accountRepository.save(newAccount);

        // When & Then: 특정 사용자가 존재하는지 확인
        Accounts retrievedAccount = accountRepository.findByLoginIdAndStatus("test123@google.com", "active").orElse(null);
        assertNotNull(retrievedAccount);
        assertEquals("test123@google.com", retrievedAccount.getLoginId());

        Members newMember = new Members();
        newMember.setAccountId(savedAccount.getId());
        newMember.setEmail("test123@google.com");
        newMember.setFullName("Hong Gildong");
        newMember.setFirstName("Hong");
        newMember.setLastName("Gildong");
        newMember.setProfileUrl("http://example.com/profile.jpg");
        Members savedMember = memberRepository.save(newMember);

        // When & Then: 특정 멤버가 존재하는지 확인
        Members retrievedMember = memberRepository.findById(savedMember.getId()).orElse(null);
        assertNotNull(retrievedMember);
        assertEquals("test123@google.com", retrievedMember.getEmail());
        assertEquals("Hong Gildong", retrievedMember.getFullName());
    }
}
