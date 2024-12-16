package com.edio.studywithcard.attachment.service;

import com.edio.common.exception.NotFoundException;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.repository.AttachmentDeckTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private final S3Service s3Service;

    private final AttachmentRepository attachmentRepository;

    private final AttachmentDeckTargetRepository attachmentDeckTargetRepository;

    /*
        파일 업로드 및 저장
     */
    @Override
    @Transactional
    public Attachment saveAttachment(MultipartFile file, String folder) {
        // 1. S3 업로드
        String filePath = s3Service.uploadFile(file, folder);

        // 2. DB 저장
        // FIXME: fileTarget을 제대로 된 값으로 수정
        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .filePath(filePath)
                .fileSize(convertFileSize(file.getSize()))
                .fileType(file.getContentType())
                .fileTarget("deck")
                .build();
        attachment = attachmentRepository.save(attachment);

        AttachmentDeckTarget attachmentDeckTarget = AttachmentDeckTarget.builder()
                .attachment(attachment)
                .build();
        attachmentDeckTargetRepository.save(attachmentDeckTarget);

        return attachment;
    }

    /*
        파일 삭제
     */
    @Override
    @Transactional
    public void deleteAttachment(String filePath) {
        Attachment existingAttachment = attachmentRepository.findByFilePathAndIsDeletedFalse(filePath)
                .orElseThrow(() -> new NotFoundException(Attachment.class, filePath));

        existingAttachment.setDeleted(true);

        s3Service.deleteFile(filePath);
    }

    public static String convertFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            return String.format("%.2f KB", sizeInBytes / 1024.0);
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", sizeInBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", sizeInBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}

