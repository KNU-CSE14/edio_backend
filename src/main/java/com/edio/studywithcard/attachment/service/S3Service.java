package com.edio.studywithcard.attachment.service;

import com.edio.studywithcard.attachment.model.response.FileInfoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {
    FileInfoResponse uploadFile(MultipartFile file, String folder);

    void deleteFiles(List<String> fileKeys);

    void deleteFile(String fileKey);
}
