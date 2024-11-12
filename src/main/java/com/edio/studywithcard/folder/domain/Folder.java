package com.edio.studywithcard.folder.domain;

import com.edio.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "folder")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Folder extends BaseEntity {

    @Column(nullable = false)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Folder> children = new ArrayList<>();

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false)
    @Setter
    @Builder.Default
    private boolean isDeleted = false;

    // 부모 폴더 설정(부모 <-> 자식 양방향)
    public void setParent(Folder parent) {
        this.parent = parent;
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

}
