package com.edio.studywithcard.attachment.service;

import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.card.dto.AttachmentBulkData;
import com.edio.studywithcard.deck.domain.Deck;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AttachmentService {
    // 첨부 파일 저장
    Attachment saveAttachment(MultipartFile file, String folder, String target) throws IOException;

    // 첨부 파일 저장(Bulk)
    void saveAllAttachments(List<AttachmentBulkData> bulkDataList);

    // Deck 중간 테이블 저장
    void saveAttachmentDeckTarget(Attachment attachment, Deck deck);

    // 첨부 파일 삭제(Bulk)
    void deleteAllAttachments(List<String> fileKeys);
}
