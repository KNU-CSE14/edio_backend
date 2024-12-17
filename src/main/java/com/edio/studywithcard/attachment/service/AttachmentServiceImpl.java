package com.edio.studywithcard.attachment.service;

import com.edio.common.exception.NotFoundException;
import com.edio.studywithcard.attachment.domain.Attachment;
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
    public Attachment saveAttachment(MultipartFile file, String folder, String target) {
        // 1. S3 업로드
        String filePath = s3Service.uploadFile(file, folder);

        // 2. DB 저장
        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .filePath(filePath)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileTarget(target)
                .build();
        return attachmentRepository.save(attachment);
    }

    /*
        파일 삭제
     */
    @Override
    @Transactional
    public void deleteAttachment(String filePath) {
        Attachment existingAttachment = attachmentRepository.findByFilePathAndIsDeletedFalse(filePath)
                .orElseThrow(() -> new NotFoundException(Attachment.class, filePath));

        // 1. DB 삭제
        existingAttachment.setDeleted(true);

        // 2. S3 삭제
        s3Service.deleteFile(filePath);
    }
}
