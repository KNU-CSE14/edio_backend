package com.edio.user.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberCreateRequest {
    String email;
    String name;
    String givenName;
    String familyName;
    String profileUrl;
}