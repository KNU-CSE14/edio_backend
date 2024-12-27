package com.edio.studywithcard.attachment.service;

import com.edio.common.exception.custom.NotFoundException;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.model.response.FileInfoResponse;
import com.edio.studywithcard.attachment.repository.AttachmentCardTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentDeckTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentRepository;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.deck.domain.Deck;
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

    private final AttachmentCardTargetRepository attachmentCardTargetRepository;

    /*
        파일 업로드 및 Attachment 저장
     */
    @Override
    @Transactional
    public Attachment saveAttachment(MultipartFile file, String folder, String target) {
        // 1. S3 업로드
        folder = folder.toLowerCase();
        FileInfoResponse fileInfo = s3Service.uploadFile(file, folder);

        // 2. DB 저장
        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .fileKey(fileInfo.fileKey())
                .filePath(fileInfo.filePath())
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileTarget(target)
                .build();
        return attachmentRepository.save(attachment);
    }

    /*
        AttachmentDeckTarget 저장
    */
    @Override
    @Transactional
    public void saveAttachmentDeckTarget(Attachment attachment, Deck deck) {
        AttachmentDeckTarget attachmentDeckTarget = AttachmentDeckTarget.builder()
                .attachment(attachment)
                .deck(deck)
                .build();
        attachmentDeckTargetRepository.save(attachmentDeckTarget);
    }

    /*
        AttachmentCardTarget 저장
    */
    @Override
    @Transactional
    public void saveAttachmentCardTarget(Attachment attachment, Card card) {
        AttachmentCardTarget attachmentCardTarget = AttachmentCardTarget.builder()
                .attachment(attachment)
                .card(card)
                .build();
        attachmentCardTargetRepository.save(attachmentCardTarget);
    }

    /*
        파일 삭제
     */
    @Override
    @Transactional
    public void deleteAttachment(String fileKey) {
        Attachment existingAttachment = attachmentRepository.findByFileKeyAndIsDeletedFalse(fileKey)
                .orElseThrow(() -> new NotFoundException(Attachment.class, fileKey));

        // 1. DB 삭제
        existingAttachment.setDeleted(true);

        // 2. S3 삭제
        s3Service.deleteFile(fileKey);
    }
}
