package com.edio.studywithcard.attachment.service;

import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.deck.domain.Deck;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AttachmentService {
    Attachment saveAttachment(MultipartFile file, String folder, String target) throws IOException;

    void saveAttachmentDeckTarget(Attachment attachment, Deck deck);

    void deleteAttachment(String filePath);
}
