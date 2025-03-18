package com.edio.studywithcard.attachment;

import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.model.response.FileInfoResponse;
import com.edio.studywithcard.attachment.repository.AttachmentCardTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentDeckTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentRepository;
import com.edio.studywithcard.attachment.service.AttachmentServiceImpl;
import com.edio.studywithcard.attachment.service.S3Service;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.dto.AttachmentBulkData;
import com.edio.studywithcard.deck.domain.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private String fileName;
    private Long fileSize;
    private String fileKey;
    private String fileType;
    private String fileTarget;
    private String s3FolderName;
    private FileInfoResponse fileInfoResponse;
    List<String> fileKeys = new ArrayList<>();

    @BeforeEach
    void setUp() {
        fileName = "test.jpg";
        fileKey = "image/test.jpg";
        fileSize = 1024L;
        fileType = "image/jpeg";
        fileTarget = "CARD";
        s3FolderName = "image";
        String filePath = "image/test.jpg";
        String bucketName = "edio-file-bucket";
        String region = "ap-northeast-2";

        // 새로운 FileInfoResponse 설정
        fileInfoResponse = new FileInfoResponse(
                String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, filePath),
                fileKey
        );

        fileKeys.add(fileKey);
    }

    @Test
    void testSaveAllAttachments() {
        // Given
        // bulkDataList에 들어갈 Dummy 파일과 Card 객체 생성
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                fileName,
                fileType,
                new byte[1024]
        );
        Card dummyCard = mock(Card.class);

        // bulk 데이터 객체 생성 (업데이트나 신규 모두 같은 방식으로 처리됨)
        AttachmentBulkData bulkData = new AttachmentBulkData(
                mockFile,
                dummyCard,
                "IMAGE",    // 폴더 (대문자로 저장됨)
                "CARD",     // 대상
                null        // 기존 파일 키 (신규 첨부라면 null)
        );
        List<AttachmentBulkData> bulkDataList = List.of(bulkData);

        // s3Service.uploadFile stub 설정
        when(s3Service.uploadFile(any(MultipartFile.class), eq("image")))
                .thenReturn(fileInfoResponse);

        // When
        attachmentService.saveAllAttachments(bulkDataList);

        // Then
        // AttachmentRepository.saveAll 호출 검증: 첨부파일 객체 리스트가 올바르게 만들어졌는지 확인
        ArgumentCaptor<List<Attachment>> attachmentsCaptor = ArgumentCaptor.forClass(List.class);
        verify(attachmentRepository, times(1)).saveAll(attachmentsCaptor.capture());
        List<Attachment> savedAttachments = attachmentsCaptor.getValue();
        assertEquals(1, savedAttachments.size());
        Attachment savedAttachment = savedAttachments.get(0);

        assertEquals(fileName, savedAttachment.getFileName());
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

        assertEquals(dummyCard, target.getCard());
        assertEquals(savedAttachment, target.getAttachment());
    }


    @Test
    void testSaveAttachment() {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                fileName,
                fileType,
                new byte[1024]
        );
        when(s3Service.uploadFile(mockFile, s3FolderName)).thenReturn(fileInfoResponse);

        Attachment mockAttachment = Attachment.builder()
                .fileName(fileName)
                .filePath(fileInfoResponse.filePath())
                .fileKey(fileInfoResponse.fileKey())
                .fileSize(fileSize)
                .fileType(fileType)
                .fileTarget(fileTarget)
                .build();

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(mockAttachment);

        // When
        Attachment result = attachmentService.saveAttachment(mockFile, s3FolderName, fileTarget);

        // Then
        assertNotNull(result);
        assertEquals(fileName, result.getFileName());
        assertEquals(fileKey, result.getFileKey());
        assertEquals(fileSize, result.getFileSize());
        verify(s3Service, times(1)).uploadFile(mockFile, s3FolderName);
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }


    @Test
    void testSaveAttachmentDeckTarget() {
        // Given
        Attachment mockAttachment = mock(Attachment.class);
        Deck mockDeck = mock(Deck.class);

        // When
        attachmentService.saveAttachmentDeckTarget(mockAttachment, mockDeck);

        // Then
        ArgumentCaptor<AttachmentDeckTarget> captor = ArgumentCaptor.forClass(AttachmentDeckTarget.class);
        verify(attachmentDeckTargetRepository, times(1)).save(captor.capture());
        assertEquals(mockAttachment, captor.getValue().getAttachment());
        assertEquals(mockDeck, captor.getValue().getDeck());
    }

    @Test
    void testDeleteAllAttachmentsSuccess() {
        // Given
        Attachment mockAttachment = mock(Attachment.class);
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
    void testDeleteAllAttachments_WhenNoAttachmentsExist() {
        // Given
        when(attachmentRepository.findAllByFileKeyInAndIsDeletedFalse(fileKeys)).thenReturn(List.of());

        // When
        attachmentService.deleteAllAttachments(fileKeys);

        // Then
        verify(attachmentRepository, times(1)).findAllByFileKeyInAndIsDeletedFalse(fileKeys);
        verify(attachmentRepository, times(1)).deleteAll(List.of());
        verifyNoMoreInteractions(attachmentRepository);

        // S3 삭제는 호출됨
        verify(s3Service, times(1)).deleteAllFiles(fileKeys);
    }
}
