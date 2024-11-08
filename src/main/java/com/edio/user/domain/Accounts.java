package com.edio.user.domain;

import com.edio.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Accounts extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private boolean isDeleted = true;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String loginType;

    @Column(nullable = false)
    private String roles;
}
