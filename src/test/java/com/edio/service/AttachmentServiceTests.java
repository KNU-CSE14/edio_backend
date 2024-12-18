package com.edio.service;

import com.edio.common.exception.NotFoundException;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.repository.AttachmentDeckTargetRepository;
import com.edio.studywithcard.attachment.repository.AttachmentRepository;
import com.edio.studywithcard.attachment.service.AttachmentServiceImpl;
import com.edio.studywithcard.attachment.service.S3Service;
import com.edio.studywithcard.deck.domain.Deck;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

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

    @Test
    void testSaveAttachment() {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(s3Service.uploadFile(mockFile, "image")).thenReturn("s3/image/test.jpg");

        Attachment mockAttachment = Attachment.builder()
                .fileName("test.jpg")
                .filePath("s3/image/test.jpg")
                .fileSize(1024L)
                .fileType("image/jpeg")
                .fileTarget("DECK")
                .build();

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(mockAttachment);

        // When
        Attachment result = attachmentService.saveAttachment(mockFile, "image", "DECK");

        // Then
        assertNotNull(result);
        assertEquals("test.jpg", result.getFileName());
        assertEquals("s3/image/test.jpg", result.getFilePath());
        assertEquals(1024L, result.getFileSize());
        verify(s3Service, times(1)).uploadFile(mockFile, "image");
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
        String filePath = "s3/image/test.jpg";
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
        String filePath = "s3/image/test.jpg";

        when(attachmentRepository.findByFilePathAndIsDeletedFalse(filePath)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> attachmentService.deleteAttachment(filePath));
        verify(attachmentRepository, times(1)).findByFilePathAndIsDeletedFalse(filePath);
        verifyNoInteractions(s3Service);
    }
}
