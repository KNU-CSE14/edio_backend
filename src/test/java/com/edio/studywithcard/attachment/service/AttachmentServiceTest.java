package com.edio.studywithcard.attachment.service;

import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.model.response.FileInfoResponse;
import com.edio.studywithcard.attachment.repository.AttachmentCardTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentDeckTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentRepository;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.dto.AttachmentBulkData;
import com.edio.studywithcard.deck.domain.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.edio.common.TestConstants.File.*;
import static com.edio.common.util.TestDataUtil.createAttachment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private AttachmentDeckTargetRepository attachmentDeckTargetRepository;

    @Mock
    private AttachmentCardTargetRepository attachmentCardTargetRepository;

    @Spy
    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private FileInfoResponse fileInfoResponse;
    List<String> fileKeys = new ArrayList<>();

    private MockMultipartFile mockFile;
    private AttachmentBulkData bulkData;
    private Card mockCard;
    private Deck mockDeck;
    private Attachment mockAttachment;

    @BeforeEach
    void setUp() {
        // S3 업로드 후 응답 객체
        fileInfoResponse = new FileInfoResponse(
                String.format(
                        FILE_PATH,
                        BUCKET_NAME,
                        REGION,
                        FILE_KEY),
                FILE_KEY
        );

        // 파일 목록
        fileKeys.add(FILE_KEY);

        // bulkData에 들어갈 Dummy 파일 생성
        mockFile = new MockMultipartFile(
                MOCK_FILE_TYPE,
                FILE_NAME,
                FILE_TYPE,
                new byte[1024]
        );

        mockCard = mock(Card.class);
        // bulkDataList에 들어갈 bulkData 생성
        bulkData = new AttachmentBulkData(
                mockFile,
                mockCard,
                FOLDER_TARGET,   // IMAGE
                FILE_TARGET,     // CARD
                null        // 기존 파일 키 (신규 첨부라면 null)
        );

        mockAttachment = createAttachment(FILE_NAME, fileInfoResponse.filePath(), fileInfoResponse.fileKey(), FILE_SIZE, FILE_TYPE, FILE_TARGET);
        mockDeck = mock(Deck.class);
    }

    @Test
    @DisplayName("첨부파일 리스트 저장 시 파일 및 카드 매핑 -> (성공)")
    void 첨부파일_리스트_저장_매핑() {
        // Given
        List<AttachmentBulkData> bulkDataList = List.of(bulkData);

        when(s3Service.uploadFile(mockFile, S3_FOLDER_NAME)).thenReturn(fileInfoResponse);

        // When
        attachmentService.saveAllAttachments(bulkDataList);

        // Then
        // AttachmentRepository.saveAll 호출 검증: 첨부파일 객체 리스트가 올바르게 만들어졌는지 확인
        ArgumentCaptor<List<Attachment>> attachmentsCaptor = ArgumentCaptor.forClass(List.class);
        verify(attachmentRepository, times(1)).saveAll(attachmentsCaptor.capture());
        List<Attachment> savedAttachments = attachmentsCaptor.getValue();
        assertEquals(1, savedAttachments.size());
        Attachment savedAttachment = savedAttachments.get(0);

        assertEquals(FILE_NAME, savedAttachment.getFileName());
        assertEquals(fileInfoResponse.fileKey(), savedAttachment.getFileKey());
        assertEquals(fileInfoResponse.filePath(), savedAttachment.getFilePath());
        assertEquals(mockFile.getSize(), savedAttachment.getFileSize());
        assertEquals(mockFile.getContentType(), savedAttachment.getFileType());
        assertEquals(bulkData.getTarget(), savedAttachment.getFileTarget());

        // AttachmentCardTargetRepository.saveAll 호출 검증: 각 Attachment와 Card 객체가 연결되어 저장되는지 확인
        ArgumentCaptor<List<AttachmentCardTarget>> targetCaptor = ArgumentCaptor.forClass(List.class);
        verify(attachmentCardTargetRepository, times(1)).saveAll(targetCaptor.capture());
        List<AttachmentCardTarget> savedTargets = targetCaptor.getValue();
        assertEquals(1, savedTargets.size());
        AttachmentCardTarget target = savedTargets.get(0);

        assertEquals(mockCard, target.getCard());
        assertEquals(savedAttachment, target.getAttachment());
    }


    @Test
    @DisplayName("첨부파일 저장 시 파일 매핑 -> (성공)")
    void 첨부파일_저장_매핑() throws IOException {
        // Given
        // convertToWebPBytes()는 실제 이미지 변환 안 하도록 가짜 결과 설정
        byte[] webpBytes = new byte[2048]; // 임의의 WebP 바이트 배열
        doReturn(webpBytes).when(attachmentService).convertToWebPBytes(any(MultipartFile.class));

        // S3 업로드 가짜 응답
        FileInfoResponse webpFileInfoResponse = new FileInfoResponse(
                String.format(FILE_PATH, BUCKET_NAME, REGION, FILE_KEY_WEBP),
                FILE_KEY_WEBP
        );
        when(s3Service.uploadFile(eq(webpBytes), eq(FILE_NAME_WEBP), eq(FILE_TYPE_WEBP), eq(S3_FOLDER_NAME)))
                .thenReturn(webpFileInfoResponse);

        // DB 저장 mock
        Attachment mockWebpAttachment = createAttachment(FILE_NAME_WEBP, webpFileInfoResponse.filePath(), webpFileInfoResponse.fileKey(), (long) webpBytes.length, FILE_TYPE_WEBP, FILE_TARGET);
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(mockWebpAttachment);

        // When
        Attachment attachment = attachmentService.saveAttachment(mockFile, S3_FOLDER_NAME, FILE_TARGET);

        // Then
        assertNotNull(attachment);
        assertEquals(FILE_NAME_WEBP, attachment.getFileName());
        assertEquals(FILE_KEY_WEBP, attachment.getFileKey());
        assertEquals(webpBytes.length, attachment.getFileSize());
        assertEquals(FILE_TYPE_WEBP, attachment.getFileType());

        verify(s3Service, times(1)).uploadFile(eq(webpBytes), eq(FILE_NAME_WEBP), eq(FILE_TYPE_WEBP), eq(S3_FOLDER_NAME));
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }


    @Test
    @DisplayName("첨부파일 저장 시 덱 타겟 매핑 -> (성공)")
    void 첨부파일_덱_타겟_매핑() {
        // When
        attachmentService.saveAttachmentDeckTarget(mockAttachment, mockDeck);

        // Then
        ArgumentCaptor<AttachmentDeckTarget> captor = ArgumentCaptor.forClass(AttachmentDeckTarget.class);
        verify(attachmentDeckTargetRepository, times(1)).save(captor.capture());
        assertEquals(mockAttachment, captor.getValue().getAttachment());
        assertEquals(mockDeck, captor.getValue().getDeck());
    }

    @Test
    @DisplayName("첨부파일 리스트 삭제 검증 -> (성공)")
    void 첨부파일_리스트_삭제_검증() {
        // Given
        List<Attachment> mockAttachments = List.of(mockAttachment);

        when(attachmentRepository.findAllByFileKeyInAndIsDeletedFalse(fileKeys)).thenReturn(mockAttachments);

        // When
        attachmentService.deleteAllAttachments(fileKeys);

        // Then
        verify(attachmentRepository, times(1)).findAllByFileKeyInAndIsDeletedFalse(fileKeys);
        verify(attachmentRepository, times(1)).deleteAll(mockAttachments);
        verify(s3Service, times(1)).deleteAllFiles(fileKeys);
    }

    @Test
    @DisplayName("첨부파일 비어있는 리스트 삭제 검증")
    void 첨부파일_빈_리스트_삭제_검증() {
        // Given
        when(attachmentRepository.findAllByFileKeyInAndIsDeletedFalse(fileKeys)).thenReturn(List.of());

        // When
        attachmentService.deleteAllAttachments(fileKeys);

        // Then
        verify(attachmentRepository, times(1)).findAllByFileKeyInAndIsDeletedFalse(fileKeys);
        verify(attachmentRepository, times(1)).deleteAll(List.of());
        verify(s3Service, times(1)).deleteAllFiles(fileKeys);
    }
}
