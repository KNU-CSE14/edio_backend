package com.edio.studywithcard.attachment.service;

import com.edio.studywithcard.attachment.model.response.FileInfoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {
    // 파일 업로드
    FileInfoResponse uploadFile(MultipartFile file, String folder);

    // 파일 변환 업로드(webp)
    FileInfoResponse uploadFile(byte[] fileBytes, String fileName, String contentType, String folder);

    void deleteAllFiles(List<String> fileKeys);
}
