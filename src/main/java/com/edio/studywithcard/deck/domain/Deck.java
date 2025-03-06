package com.edio.studywithcard.deck.domain;

import com.edio.common.domain.BaseEntity;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.folder.domain.Folder;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deck")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@SQLDelete(sql = "UPDATE deck SET is_deleted = true WHERE id = ?")
public class Deck extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)
    @Setter
    private Folder folder;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Setter
    private Category category;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AttachmentDeckTarget> attachmentDeckTargets = new ArrayList<>();

    /*
        TODO: 현재, Deck 1개에 대한 Card 조회(FROM card WHERE deckId = ?)가 이루어져서 Batch Size가 불필요
         추후에 여러 Deck을 한 번에  조회할 상황(사용자 기준 모든 덱 조회)이 생기면 Batch Size 적용 여부 검토
     */
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Card> cards = new ArrayList<>();

    @Column(nullable = false)
    @Setter
    private String name;

    @Setter
    private String description;

    @Column(nullable = false)
    @Builder.Default
    @Setter
    private boolean isFavorite = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isShared = false;
}
