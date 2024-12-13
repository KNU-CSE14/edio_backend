package com.edio.studywithcard.deck.domain;

import com.edio.common.domain.BaseEntity;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.folder.domain.Folder;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "deck")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Deck extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)
    @Setter
    private Folder folder;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Setter
    private Category category;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    private String description;

    @Column(nullable = false)
    @Builder.Default
    @Setter
    private boolean isFavorite = false;

    @Column(nullable = false)
    @Builder.Default
    @Setter
    private boolean isDeleted = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isShared = false;
}
