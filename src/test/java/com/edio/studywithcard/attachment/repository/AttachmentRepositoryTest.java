package com.edio.studywithcard.attachment.repository;

import com.edio.common.config.JpaConfig;
import com.edio.studywithcard.attachment.domain.Attachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static com.edio.common.TestConstants.File.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
public class AttachmentRepositoryTest {

    @Autowired
    private AttachmentRepository attachmentRepository;

    private Attachment testAttachment;

    @BeforeEach
    void setUp() {
        testAttachment = attachmentRepository.save(Attachment.builder()
                .fileName(FILE_NAME)
                .filePath(FILE_PATH)
                .fileKey(FILE_KEY)
                .fileSize(FILE_SIZE)
                .fileType(FILE_TYPE)
                .fileTarget(FILE_TARGET)
                .build());
    }

    @Test
    @DisplayName("첨부파일 조회 동작 확인 -> (성공)")
    void findAllByFileKeyInAndIsDeletedFalseTest() {
        // When
        List<Attachment> attachments = attachmentRepository.findAllByFileKeyInAndIsDeletedFalse(List.of(FILE_KEY));

        // Then
        assertThat(attachments).hasSize(1);
        assertThat(attachments.get(0).getFileKey()).isEqualTo(FILE_KEY);
    }

    @Test
    @DisplayName("첨부파일 Soft Delete 동작 확인 -> (성공)")
    void softDeleteAttachment() {
        // When
        attachmentRepository.deleteAll(List.of(testAttachment));
        attachmentRepository.flush();

        // Then
        Optional<Attachment> attachment = attachmentRepository.findById(testAttachment.getId());
        assertThat(attachment).isPresent();
        assertThat(attachment.get().isDeleted()).isTrue();

        // isDeleted = false 조건으로 조회
        List<Attachment> attachments = attachmentRepository.findAllByFileKeyInAndIsDeletedFalse(List.of(FILE_KEY));
        assertThat(attachments).isEmpty();
    }
}
