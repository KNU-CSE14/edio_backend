package com.edio.studywithcard.folder.domain;

import com.edio.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "folder")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Folder extends BaseEntity {

    @Column(nullable = false)
    private Long accountId;

    @Setter
    private Long parentId;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false)
    @Setter
    @Builder.Default
    private boolean isDeleted = false;


}
