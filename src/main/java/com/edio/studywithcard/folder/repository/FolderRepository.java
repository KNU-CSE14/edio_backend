package com.edio.studywithcard.folder.repository;

import com.edio.studywithcard.folder.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByAccountIdAndNameAndStatus(Long accountId, String name, boolean status);
    List<Folder> findByAccountIdAndStatus(Long accountId, boolean status);
    Optional<Folder> findByIdAndStatus(Long id, boolean status);
}
