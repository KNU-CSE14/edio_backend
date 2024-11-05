package com.edio.user.domain;

import com.edio.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

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

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String loginType;

    @Column(nullable = false)
    private String roles;
}
