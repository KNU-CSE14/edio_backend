package com.edio.studywithcard.attachment.service;

import com.edio.studywithcard.attachment.domain.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AttachmentService {
    Attachment saveAttachment(MultipartFile file, String folder) throws IOException;

    void deleteAttachment(String filePath);
}
