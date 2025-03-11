package com.edio.studywithcard.folder.domain;

import com.edio.common.domain.BaseEntity;
import com.edio.studywithcard.deck.domain.Deck;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "folder")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@SQLDelete(sql = "UPDATE folder SET is_deleted = true, updated_at = CURRENT_TIMESTAMP(6) WHERE id = ?")
public class Folder extends BaseEntity {

    @Column(nullable = false)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 10)
    private List<Folder> childrenFolders = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 10)
    private List<Deck> decks = new ArrayList<>();

    @Column(nullable = false)
    @Setter
    private String name;

    public void setParentFolder(Folder parentFolder) {
        // 새로운 부모 폴더 설정
        this.parentFolder = parentFolder;
        if (parentFolder != null && !parentFolder.getChildrenFolders().contains(this)) {
            parentFolder.getChildrenFolders().add(this);
        }
    }

}
