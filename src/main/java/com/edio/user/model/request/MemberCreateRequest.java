package com.edio.user.model.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberCreateRequest {
    Long accountId;
    String email;
    String name;
    String givenName;
    String familyName;
    String profileUrl;
}