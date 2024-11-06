package com.edio.user.domain;


import com.edio.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Members extends BaseEntity {

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String givenName;

    @Column(nullable = false)
    private String familyName;

    private String profileUrl;
}
