package com.edio.studywithcard.attachment.model.response;

import com.edio.studywithcard.attachment.domain.Attachment;

public record AttachmentResponse(
        Long id,
        String fileName,
        String fileType,
        String filePath,
        String fileSize,
        String fileTarget
) {
    public static AttachmentResponse from(Attachment attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getFileType(),
                attachment.getFilePath(),
                attachment.getFileSize(),
                attachment.getFileTarget()
        );
    }
}
