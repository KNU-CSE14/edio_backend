package com.edio.studywithcard.attachment.domain;

import com.edio.common.domain.BaseEntity;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "attachment_deck_target_id", nullable = false)
    private AttachmentDeckTarget attachmentDeckTarget;
}
