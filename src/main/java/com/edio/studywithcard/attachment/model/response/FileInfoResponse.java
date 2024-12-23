package com.edio.studywithcard.attachment.model.response;

public record FileInfoResponse(
        String filePath,
        String fileKey
) {
    public static FileInfoResponse from(String filePath, String fileKey) {
        return new FileInfoResponse(
                filePath,
                fileKey
        );
    }
}
