package com.edio.studywithcard.attachment.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachment_deck_target")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AttachmentDeckTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;
}
