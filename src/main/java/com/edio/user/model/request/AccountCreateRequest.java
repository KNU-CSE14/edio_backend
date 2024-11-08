package com.edio.user.model.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountCreateRequest {
    private String loginId;
}