package com.edio.user.domain;

import com.edio.common.domain.BaseEntity;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Accounts extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private boolean status = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountLoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountRole roles;
}
