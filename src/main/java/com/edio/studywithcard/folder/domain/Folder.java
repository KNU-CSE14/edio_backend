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
@Builder(toBuilder = true)
public class Folder extends BaseEntity {

    @Column(nullable = false)
    private Long accountId;

    private Long parentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private boolean status = true;

    public Folder updateFields(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
        return this;
    }

    public Folder deleteeFields(boolean status) {
        this.status = status;
        return this;
    }
}
