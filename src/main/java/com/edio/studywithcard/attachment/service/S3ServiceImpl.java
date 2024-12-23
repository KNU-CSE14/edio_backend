package com.edio.studywithcard.attachment.service;

import com.edio.common.exception.InternalServerException;
import com.edio.studywithcard.attachment.model.response.FileInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    private final String bucketName = System.getProperty("AWS_BUCKET_NAME");

    private final String region = System.getProperty("AWS_REGION");

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

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
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (AwsServiceException | SdkClientException e) {
            log.error("AWS 서비스 오류 발생 - 파일 등록 실패: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        } catch (IOException e) {
            log.error("알 수 없는 오류 발생 - 파일 등록 실패: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }

        return FileInfoResponse.from(String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName), fileName);
    }

    /*
        S3 파일 삭제
     */
    @Override
    public void deleteFile(String fileKey) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileKey)
                            .build()
            );
        } catch (AwsServiceException | SdkClientException e) {
            log.error("AWS 서비스 오류 발생 - 파일 삭제 실패: {}", fileKey, e);
            throw new InternalServerException(e.getMessage());
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생 - 파일 삭제 실패: {}", fileKey, e);
            throw new InternalServerException(e.getMessage());
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
