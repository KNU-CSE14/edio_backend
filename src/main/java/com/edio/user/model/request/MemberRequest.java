package com.edio.user.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberRequest {
    Long accountId;
    String email;
    String name;
    String givenName;
    String familyName;
    String profileUrl;
}