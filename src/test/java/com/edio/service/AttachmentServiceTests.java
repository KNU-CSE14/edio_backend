package com.edio.service;

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
import com.edio.studywithcard.deck.domain.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTests {

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
        fileTarget = "DECK";
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
    void testSaveAttachmentCardTarget() {
        // Given
        Attachment mockAttachment = mock(Attachment.class);
        Card mockCard = mock(Card.class);

        // When
        attachmentService.saveAttachmentCardTarget(mockAttachment, mockCard);

        // Then
        ArgumentCaptor<AttachmentCardTarget> captor = ArgumentCaptor.forClass(AttachmentCardTarget.class);
        verify(attachmentCardTargetRepository, times(1)).save(captor.capture());
        assertEquals(mockAttachment, captor.getValue().getAttachment());
        assertEquals(mockCard, captor.getValue().getCard());
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
        verify(mockAttachment, times(1)).setDeleted(true);
        verify(attachmentRepository, times(1)).findAllByFileKeyInAndIsDeletedFalse(fileKeys);
        verify(s3Service, times(1)).deleteFiles(fileKeys);
    }

    @Test
    void testDeleteAllAttachments_WhenNoAttachmentsExist() {
        // Given
        when(attachmentRepository.findAllByFileKeyInAndIsDeletedFalse(fileKeys)).thenReturn(List.of());

        // When
        attachmentService.deleteAllAttachments(fileKeys);

        // Then
        verify(attachmentRepository, times(1)).findAllByFileKeyInAndIsDeletedFalse(fileKeys);
        verifyNoMoreInteractions(attachmentRepository);

        // S3 삭제는 호출됨
        verify(s3Service, times(1)).deleteFiles(fileKeys);
    }
}
