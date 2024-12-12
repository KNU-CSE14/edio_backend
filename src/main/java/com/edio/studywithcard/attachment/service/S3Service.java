package com.edio.studywithcard.attachment.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadFile(MultipartFile file, String folder);

    void deleteFile(String filePath);
}
