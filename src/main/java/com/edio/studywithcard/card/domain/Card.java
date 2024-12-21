package com.edio.studywithcard.card.domain;

import com.edio.common.domain.BaseEntity;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.deck.domain.Deck;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "card")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Card extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    @Setter
    private Deck deck;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    @Setter
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<AttachmentCardTarget> attachmentCardTargets = new ArrayList<>();
}
