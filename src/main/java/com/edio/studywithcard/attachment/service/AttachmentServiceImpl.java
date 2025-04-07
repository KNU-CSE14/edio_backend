package com.edio.studywithcard.attachment.service;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.domain.enums.AttachmentFolder;
import com.edio.studywithcard.attachment.model.response.FileInfoResponse;
import com.edio.studywithcard.attachment.repository.AttachmentCardTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentDeckTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentRepository;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.dto.AttachmentBulkData;
import com.edio.studywithcard.deck.domain.Deck;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private static final String MIME_TYPE_WEBP = "image/webp";
    private static final String FILE_EXTENSION_WEBP = ".webp";

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
        덱 파일 webp 변환 업로드 및 Attachment 저장
     */
    @Override
    @Transactional
    public Attachment saveAttachment(MultipartFile file, String folder, String target) {
        // 이미지가 아닐 경우 예외 처리
        if (file.getContentType() == null || !file.getContentType().startsWith(AttachmentFolder.IMAGE.name().toLowerCase())) {
            log.error("{}: filename = {}, contentType = {}", ErrorMessages.FILE_PROCESSING_UNSUPPORTED.getMessage(), file.getOriginalFilename(), file.getContentType());
            throw new UnsupportedOperationException(ErrorMessages.FILE_PROCESSING_UNSUPPORTED.getMessage());
        }

        try {
            // WebP 변환 및 업로드
            String fileName = FilenameUtils.getBaseName(file.getOriginalFilename()) + FILE_EXTENSION_WEBP;

            byte[] webpBytes = convertToWebPBytes(file);
            FileInfoResponse fileInfo = s3Service.uploadFile(webpBytes, fileName, MIME_TYPE_WEBP, folder.toLowerCase());

            // 3. DB 저장
            return attachmentRepository.save(
                    Attachment.builder()
                            .fileName(fileName)
                            .fileKey(fileInfo.fileKey())
                            .filePath(fileInfo.filePath())
                            .fileSize((long) webpBytes.length)
                            .fileType(MIME_TYPE_WEBP)
                            .fileTarget(target)
                            .build()
            );
        } catch (IOException e) {
            log.error("알 수 없는 오류 발생 - 파일 변환 실패: {}", e.getMessage(), e);
            throw new RuntimeException(ErrorMessages.INTERNAL_SERVER_ERROR.getMessage());
        }
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
        attachmentRepository.deleteAll(attachments);

        // 2. S3에서 파일 삭제
        s3Service.deleteAllFiles(fileKeys);
    }

    /*
        WebP 변환
     */
    public byte[] convertToWebPBytes(MultipartFile file) throws IOException {
        ImmutableImage image = ImmutableImage.loader().fromStream(file.getInputStream());
        WebpWriter writer = new WebpWriter();
        return image.bytes(writer);
    }
}
