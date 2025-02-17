package com.edio.service;

import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.attachment.domain.enums.AttachmentFolder;
import com.edio.studywithcard.attachment.domain.enums.FileTarget;
import com.edio.studywithcard.attachment.service.AttachmentService;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.model.request.CardBulkRequest;
import com.edio.studywithcard.card.model.request.CardBulkRequestWrapper;
import com.edio.studywithcard.card.repository.CardRepository;
import com.edio.studywithcard.card.service.CardServiceImpl;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.repository.DeckRepository;
import com.edio.studywithcard.folder.domain.Folder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTests {

    private static final String IMAGE_MIME_JPEG = "image/jpeg";
    private static final String AUDIO_MIME_MPEG = "audio/mpeg";
    private static final Long accountId = 1L;
    private static final Long deckId = 1L;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private CardServiceImpl cardService;

    private Deck dummyDeck;
    private Folder dummyFolder;

    @BeforeEach
    void setUp() {
        dummyDeck = mock(Deck.class);
        dummyFolder = mock(Folder.class);

        when(dummyFolder.getAccountId()).thenReturn(accountId);
        when(dummyDeck.getFolder()).thenReturn(dummyFolder);
    }

    // ==================== 헬퍼 메서드 ====================

    private CardBulkRequest createCardRequest(Long cardId, String name, String description) {
        CardBulkRequest request = new CardBulkRequest();
        request.setCardId(cardId);
        request.setDeckId(deckId);
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    private MultipartFile mockMultipartFile(boolean isEmpty, String contentType) {
        MultipartFile file = mock(MultipartFile.class);
        lenient().when(file.isEmpty()).thenReturn(isEmpty);
        lenient().when(file.getContentType()).thenReturn(contentType);
        return file;
    }

    private CardBulkRequestWrapper createWrapper(CardBulkRequest request) {
        CardBulkRequestWrapper wrapper = new CardBulkRequestWrapper() {};
        wrapper.setRequests(Collections.singletonList(request));
        return wrapper;
    }

    // ==================== 테스트 ====================

    @Test
    void 신규_카드_생성_첨부파일_포함_테스트() throws Exception {
        // Given: 신규 카드 생성 요청 및 첨부파일 모킹
        CardBulkRequest request = createCardRequest(null, "Card with attachments", "Description with attachments");
        MultipartFile imageFile = mockMultipartFile(false, IMAGE_MIME_JPEG);
        MultipartFile audioFile = mockMultipartFile(false, AUDIO_MIME_MPEG);
        request.setImage(imageFile);
        request.setAudio(audioFile);
        CardBulkRequestWrapper wrapper = createWrapper(request);

        // 덱 조회 스텁 (요청에 사용된 deckId에 대해 dummyDeck 반환)
        when(deckRepository.findById(eq(request.getDeckId()))).thenReturn(Optional.of(dummyDeck));

        // 첨부파일 저장 시 더미 Attachment 반환 설정
        // 이미지 저장 -> imageAttachment("imageKey")가 반환
        // 오디오 저장 -> audioAttachment("audioKey")가 반환
        Attachment imageAttachment = Attachment.builder().fileKey("imageKey").build();
        Attachment audioAttachment = Attachment.builder().fileKey("audioKey").build();
        when(attachmentService.saveAttachment(eq(imageFile), eq(AttachmentFolder.IMAGE.name()), eq(FileTarget.CARD.name())))
                .thenReturn(imageAttachment);
        when(attachmentService.saveAttachment(eq(audioFile), eq(AttachmentFolder.AUDIO.name()), eq(FileTarget.CARD.name())))
                .thenReturn(audioAttachment);

        // When: 카드 생성 실행
        cardService.upsert(accountId, wrapper);

        // Then: 카드 저장 및 첨부파일 처리 검증

        // cardRepository.saveAll()이 한 번 호출되었는지 검증
        ArgumentCaptor<List<Card>> cardCaptor = ArgumentCaptor.forClass(List.class);
        verify(cardRepository, times(1)).saveAll(cardCaptor.capture());

        // 저장된 카드가 1개인지 검증
        // 카드 정보가 올바르게 저장되었는지 확인
        List<Card> savedCards = cardCaptor.getValue();
        assertEquals(1, savedCards.size());
        Card savedCard = savedCards.get(0);
        assertEquals("Card with attachments", savedCard.getName());
        assertEquals("Description with attachments", savedCard.getDescription());
        assertEquals(dummyDeck, savedCard.getDeck());

        // 첨부 파일이 올바르게 저장되었는지 검증
        verify(attachmentService, times(1)).saveAttachment(eq(imageFile), eq(AttachmentFolder.IMAGE.name()), eq(FileTarget.CARD.name()));
        verify(attachmentService, times(1)).saveAttachment(eq(audioFile), eq(AttachmentFolder.AUDIO.name()), eq(FileTarget.CARD.name()));
        verify(attachmentService, times(1)).saveAttachmentCardTarget(imageAttachment, savedCard);
        verify(attachmentService, times(1)).saveAttachmentCardTarget(audioAttachment, savedCard);
    }

    @Test
    void 기존_카드_수정_첨부파일_업데이트_테스트() throws Exception {
        // Given: 기존 카드 수정 요청
        Long cardId = 1L;
        CardBulkRequest request = createCardRequest(cardId,  "Updated Name", "Updated Description");

        // 새로운 파일
        MultipartFile newImageFile = mockMultipartFile(false, IMAGE_MIME_JPEG);
        MultipartFile emptyAudioFile = mockMultipartFile(true, AUDIO_MIME_MPEG);
        request.setImage(newImageFile);
        request.setAudio(emptyAudioFile);
        CardBulkRequestWrapper wrapper = createWrapper(request);

        // 기존 카드 및 첨부파일 설정
        Card existingCard = Card.builder()
                .name("Old Name")
                .description("Old Description")
                .build();
        // 기존에 저장된 이미지, 오디오 첨부파일 설정
        Attachment oldImageAttachment = Attachment.builder().fileKey("oldImageKey").fileType(IMAGE_MIME_JPEG).build();
        Attachment oldAudioAttachment = Attachment.builder().fileKey("oldAudioKey").fileType(AUDIO_MIME_MPEG).build();

        // Entity에서는 Setter를 제공하지 않기 때문에 ReflectionTestUtils로 값 지정
        AttachmentCardTarget imageTarget = new AttachmentCardTarget() {};
        ReflectionTestUtils.setField(imageTarget, "attachment", oldImageAttachment);
        AttachmentCardTarget audioTarget = new AttachmentCardTarget() {};
        ReflectionTestUtils.setField(audioTarget, "attachment", oldAudioAttachment);
        ReflectionTestUtils.setField(existingCard, "attachmentCardTargets", List.of(imageTarget, audioTarget));

        // repository 스텁 설정: cardId에 대해 기존 카드 반환
        when(cardRepository.findByIdAndIsDeletedFalse(cardId)).thenReturn(Optional.of(existingCard));
        when(deckRepository.findById(eq(request.getDeckId()))).thenReturn(Optional.of(dummyDeck));

        // 새 이미지 저장 시 더미 Attachment 반환 설정
        Attachment newImageAttachment = Attachment.builder().fileKey("newImageKey").build();
        when(attachmentService.saveAttachment(eq(newImageFile), eq(AttachmentFolder.IMAGE.name()), eq(FileTarget.CARD.name())))
                .thenReturn(newImageAttachment);

        // When: 기존 카드 업데이트 실행
        cardService.upsert(accountId, wrapper);

        // Then: 카드 정보 업데이트 검증
        assertEquals("Updated Name", existingCard.getName());
        assertEquals("Updated Description", existingCard.getDescription());

        // 이미지: 기존 이미지 삭제 후 새 이미지 저장 및 연결 검증
        verify(attachmentService, times(1)).deleteAttachment("oldImageKey");
        verify(attachmentService, times(1)).saveAttachment(eq(newImageFile), eq(AttachmentFolder.IMAGE.name()), eq(FileTarget.CARD.name()));
        verify(attachmentService, times(1)).saveAttachmentCardTarget(newImageAttachment, existingCard);

        // 오디오: 빈 파일이면 기존 오디오 삭제만 검증
        verify(attachmentService, times(1)).deleteAttachment("oldAudioKey");
        verify(attachmentService, never()).saveAttachment(eq(emptyAudioFile), eq(AttachmentFolder.AUDIO.name()), eq(FileTarget.CARD.name()));
    }
}
