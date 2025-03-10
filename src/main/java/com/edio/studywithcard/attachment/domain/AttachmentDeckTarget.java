package com.edio.studywithcard.attachment.domain;

import com.edio.studywithcard.deck.domain.Deck;
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
    @JoinColumn(name = "deck_id")
    private Deck deck;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;
}
