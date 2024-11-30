package com.edio.user.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountCreateRequest {
    private String loginId;
    private Long memberId;
}