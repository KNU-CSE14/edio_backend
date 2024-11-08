package com.edio.user.model.reponse;

import com.edio.user.domain.Accounts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AccountResponse {

    private Long id;
    private String loginId;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private String status;
    private String loginType;
    private String roles;

    public static AccountResponse from(Accounts account) {
        return AccountResponse.builder()
                .id(account.getId())
                .loginId(account.getLoginId())
                .password(account.getPassword())
                .createdAt(account.getCreatedAt())
                .deletedAt(account.getDeletedAt())
                .status(account.getStatus())
                .loginType(account.getLoginType())
                .roles(account.getRoles())
                .build();
    }
}
