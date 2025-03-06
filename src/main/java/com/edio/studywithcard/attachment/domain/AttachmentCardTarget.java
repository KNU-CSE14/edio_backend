package com.edio.studywithcard.attachment.domain;

import com.edio.studywithcard.card.domain.Card;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachment_card_target")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AttachmentCardTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;
}
