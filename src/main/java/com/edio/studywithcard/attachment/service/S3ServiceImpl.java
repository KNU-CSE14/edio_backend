package com.edio.studywithcard.attachment.service;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.common.properties.AwsProperties;
import com.edio.studywithcard.attachment.model.response.FileInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    private final AwsProperties awsProperties;

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10MB

    /*
        S3 파일 업로드
     */
    @Override
    public FileInfoResponse uploadFile(MultipartFile file, String folder) {
        // 파일 크기 검증
        validateFileSize(file);

        // 고유 파일명 생성
        String fileName = generateFileName(folder, file.getOriginalFilename());
        try {
            // 업로드
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(awsProperties.bucketName())
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException | S3Exception e) {
            log.error("알 수 없는 오류 발생 - 파일 등록 실패: {}", e.getMessage(), e);
            throw new RuntimeException(ErrorMessages.INTERNAL_SERVER_ERROR.getMessage());
        }

        return FileInfoResponse.from(String.format("https://%s.s3.%s.amazonaws.com/%s", awsProperties.bucketName(), awsProperties.region(), fileName), fileName);
    }

    /*
        S3 파일 삭제
     */
    @Override
    public void deleteAllFiles(List<String> fileKeys) {
        if (fileKeys.isEmpty()) return;
        
        try {
            // S3 DeleteObjectsRequest 생성
            List<ObjectIdentifier> objectIdentifiers = fileKeys.stream()
                    .map(fileKey -> ObjectIdentifier.builder().key(fileKey).build())
                    .collect(Collectors.toList());

            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(awsProperties.bucketName())
                    .delete(d -> d.objects(objectIdentifiers))
                    .build();

            // S3 API 호출로 다중 파일 삭제
            s3Client.deleteObjects(deleteObjectsRequest);

            log.info("Successfully deleted files: {}", fileKeys);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생 - 파일 벌크 삭제 실패: {}", fileKeys, e);
            throw new RuntimeException(ErrorMessages.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    /*
        S3 업로드 파일명 생성
     */
    private String generateFileName(String folder, String originalFileName) {
        return String.format("%s/%s_%s", folder, UUID.randomUUID(), originalFileName);
    }

    /*
        File Size 검증
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
        }
    }
}
