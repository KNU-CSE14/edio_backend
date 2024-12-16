package com.edio.studywithcard.attachment.service;

import com.edio.common.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    /*
        파일 업로드
     */
    @Override
    public String uploadFile(MultipartFile file, String folder) {
        // 고유 파일명 생성
        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            // S3에 업로드
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException e) {
            log.error("로컬 파일 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        } catch (AwsServiceException e) {
            log.error("AWS S3 서비스 오류 발생: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        } catch (SdkClientException e) {
            log.error("AWS S3 업로드 중 클라이언트 오류 발생: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
        return fileName;
    }

    /*
        파일 삭제
     */
    @Override
    public void deleteFile(String filePath) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(filePath)
                            .build()
            );
        } catch (AwsServiceException e) {
            log.error("AWS 서비스 오류 발생 - 파일 삭제 실패: {}", filePath, e);
            throw new InternalServerException(e.getMessage());
        } catch (SdkClientException e) {
            log.error("AWS 클라이언트 오류 발생 - 파일 삭제 실패: {}", filePath, e);
            throw new InternalServerException(e.getMessage());
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생 - 파일 삭제 실패: {}", filePath, e);
            throw new InternalServerException(e.getMessage());
        }
    }
}
