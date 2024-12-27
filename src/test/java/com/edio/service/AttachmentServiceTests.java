package com.edio.service;

import com.edio.common.exception.custom.NotFoundException;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.repository.AttachmentDeckTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentRepository;
import com.edio.studywithcard.attachment.service.AttachmentServiceImpl;
import com.edio.studywithcard.attachment.service.S3Service;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String fileTarget;
    private String s3FolderName;

    @BeforeEach
    void setUp() {
        fileName = "test.jpg";
        filePath = "s3/image/test.jpg";
        fileSize = 1024L;
        fileType = "image/jpeg";
        fileTarget = "DECK";
        s3FolderName = "image";
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
        when(s3Service.uploadFile(mockFile, s3FolderName)).thenReturn(filePath);

        Attachment mockAttachment = Attachment.builder()
                .fileName(fileName)
                .filePath(filePath)
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
        assertEquals(filePath, result.getFilePath());
        assertEquals(fileSize, result.getFileSize());
        verify(s3Service, times(1)).uploadFile(mockFile, s3FolderName);
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }


    @Test
    void testSaveAttachmentDeckTarget() {
        // Given
        Attachment mockAttachment = mock(Attachment.class);
        Deck mockDeck = mock(Deck.class);

        when(mockDeck.getAttachmentDeckTargets()).thenReturn(new ArrayList<>());

        // When
        attachmentService.saveAttachmentDeckTarget(mockAttachment, mockDeck);

        // Then
        ArgumentCaptor<AttachmentDeckTarget> captor = ArgumentCaptor.forClass(AttachmentDeckTarget.class);
        verify(attachmentDeckTargetRepository, times(1)).save(captor.capture());
        assertEquals(mockAttachment, captor.getValue().getAttachment());
        assertEquals(mockDeck, captor.getValue().getDeck());
        verify(mockDeck, times(1)).getAttachmentDeckTargets();
    }

    @Test
    void testDeleteAttachmentSuccess() {
        // Given
        Attachment mockAttachment = mock(Attachment.class);

        when(attachmentRepository.findByFilePathAndIsDeletedFalse(filePath)).thenReturn(Optional.of(mockAttachment));

        // When
        attachmentService.deleteAttachment(filePath);

        // Then
        verify(mockAttachment, times(1)).setDeleted(true);
        verify(attachmentRepository, times(1)).findByFilePathAndIsDeletedFalse(filePath);
        verify(s3Service, times(1)).deleteFile(filePath);
    }

    @Test
    void testDeleteAttachmentNotFound() {
        // Given
        when(attachmentRepository.findByFilePathAndIsDeletedFalse(filePath)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> attachmentService.deleteAttachment(filePath));
        verify(attachmentRepository, times(1)).findByFilePathAndIsDeletedFalse(filePath);
        verifyNoInteractions(s3Service);
    }
}
