package com.edio.studywithcard.folder.repository;

import com.edio.studywithcard.folder.domain.Folder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    /**
     * Folder 조회
     *
     * @param folderId
     * @return
     */
    Optional<Folder> findByIdAndIsDeletedFalse(Long folderId);

    /**
     * Folder 조회(전체)
     * TODO: OneToMany 관계는 EntityGraph 제거하고 필요에 따라서 Batch Size 적용 여부 확인 필요
     *
     * @param folderId
     * @return
     */
    @EntityGraph(attributePaths = {"childrenFolders", "decks"})
    Optional<Folder> findById(Long folderId);

    /**
     * Account Folder 조회
     *
     * @param accountId
     * @return
     */
    List<Folder> findByAccountIdAndIsDeletedFalse(Long accountId);
}
