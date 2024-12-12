package com.edio.studywithcard.attachment.domain;

import com.edio.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "attachment")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Attachment extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileSize;

    @Column(nullable = false)
    private String fileTarget;

    @Column(nullable = false)
    @Builder.Default
    @Setter
    private boolean isDeleted = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isShared = false;
}
