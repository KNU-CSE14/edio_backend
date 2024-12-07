package com.edio.studywithcard.folder.repository;

import com.edio.studywithcard.folder.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByAccountIdAndParentFolderIdAndIsDeleted(Long accountId, Long rootFolderId, boolean isDeleted);

    List<Folder> findAllByAccountIdAndParentFolderIsNullAndIsDeleted(Long accountId, boolean isDeleted);

    Optional<Folder> findByIdAndIsDeleted(Long id, boolean isDeleted);
}
