package com.edio.studywithcard.attachment.service;

import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.model.response.FileInfoResponse;
import com.edio.studywithcard.attachment.repository.AttachmentCardTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentDeckTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentRepository;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.dto.AttachmentBulkData;
import com.edio.studywithcard.deck.domain.Deck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private final S3Service s3Service;

    private final AttachmentRepository attachmentRepository;

    private final AttachmentDeckTargetRepository attachmentDeckTargetRepository;

    private final AttachmentCardTargetRepository attachmentCardTargetRepository;

    /*
        S3 업로드 및 Attachment/AttachmentCardTarget 객체를 벌크로 생성하고 저장하는 메서드
    */
    @Override
    @Transactional
    public void saveAllAttachments(List<AttachmentBulkData> bulkDataList) {
        // 병렬 스트림을 사용하여 각 파일에 대해 S3 업로드 및 Attachment 객체 생성
        List<Pair<Attachment, Card>> attachmentCardPairs = bulkDataList.parallelStream()
                .map(data -> {
                    // S3 업로드
                    FileInfoResponse fileInfo = s3Service.uploadFile(data.getFile(), data.getFolder().toLowerCase());

                    // Attachment 객체 생성
                    Attachment attachment = Attachment.builder()
                            .fileName(data.getFile().getOriginalFilename())
                            .fileKey(fileInfo.fileKey())
                            .filePath(fileInfo.filePath())
                            .fileSize(data.getFile().getSize())
                            .fileType(data.getFile().getContentType())
                            .fileTarget(data.getTarget())
                            .build();
                    return Pair.of(attachment, data.getCard());
                })
                .toList();

        // Attachment 및 AttachmentCardTarget 객체 리스트 생성
        List<Attachment> attachments = new ArrayList<>();
        List<AttachmentCardTarget> attachmentCardTargets = new ArrayList<>();
        for (Pair<Attachment, Card> pair : attachmentCardPairs) {
            attachments.add(pair.getFirst());
            AttachmentCardTarget attachmentCardTarget = AttachmentCardTarget.builder()
                    .attachment(pair.getFirst())
                    .card(pair.getSecond())
                    .build();
            attachmentCardTargets.add(attachmentCardTarget);
        }

        // DB에 벌크 저장 (saveAll 메서드를 사용)
        attachmentRepository.saveAll(attachments);

        // AttachmentCardTarget 저장
        attachmentCardTargetRepository.saveAll(attachmentCardTargets);
    }

    /*
        파일 업로드 및 Attachment 저장 (Deck에서 사용)
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
        AttachmentDeckTarget 저장 (Deck에서 사용)
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
        다중 파일 삭제
     */
    @Override
    @Transactional
    public void deleteAllAttachments(List<String> fileKeys) {
        // 1. DB 삭제 처리
        List<Attachment> attachments = attachmentRepository.findAllByFileKeyInAndIsDeletedFalse(fileKeys);
        attachments.forEach(attachment -> attachment.setDeleted(true));

        // 2. S3에서 파일 삭제
        s3Service.deleteFiles(fileKeys);
    }
}
