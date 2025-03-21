package com.edio.user.domain;

import com.edio.common.domain.BaseEntity;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Account extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String loginId;

    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Setter
    private Long rootFolderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountLoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountRole roles;
}
