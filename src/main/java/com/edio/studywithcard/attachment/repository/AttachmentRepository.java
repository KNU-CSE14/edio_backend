package com.edio.studywithcard.attachment.repository;

import com.edio.studywithcard.attachment.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Optional<Attachment> findByFilePathAndIsDeletedFalse(String filePath);
}
