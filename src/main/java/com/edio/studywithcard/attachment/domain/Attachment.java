package com.edio.studywithcard.attachment.domain;

import com.edio.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attachment")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@SQLDelete(sql = "UPDATE attachment SET is_deleted = true WHERE id = ?")
@BatchSize(size = 10)
public class Attachment extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String fileTarget;

    @Column(nullable = false)
    private String fileKey;

    @Column(nullable = false)
    @Builder.Default
    private boolean isShared = false;

    @OneToMany(mappedBy = "attachment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AttachmentDeckTarget> attachmentDeckTargets = new ArrayList<>();

    @OneToMany(mappedBy = "attachment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AttachmentCardTarget> attachmentCardTargets = new ArrayList<>();
}
