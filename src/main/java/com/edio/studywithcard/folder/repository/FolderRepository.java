package com.edio.studywithcard.folder.repository;

import com.edio.studywithcard.folder.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    // FolderID 조회
    Optional<Folder> findByIdAndIsDeletedFalse(Long id);

    // AccountID 조회
    List<Folder> findByAccountIdAndIsDeletedFalse(Long id);
}
