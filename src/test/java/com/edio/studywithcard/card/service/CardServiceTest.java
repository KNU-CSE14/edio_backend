package com.edio.studywithcard.card.service;

import com.edio.common.TestConstants;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.attachment.domain.enums.AttachmentFolder;
import com.edio.studywithcard.attachment.domain.enums.FileTarget;
import com.edio.studywithcard.attachment.service.AttachmentService;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.dto.AttachmentBulkData;
import com.edio.studywithcard.card.model.request.CardBulkRequest;
import com.edio.studywithcard.card.model.request.CardBulkRequestWrapper;
import com.edio.studywithcard.card.repository.CardRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.repository.DeckRepository;
import com.edio.studywithcard.folder.domain.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private CardServiceImpl cardService;

    private Deck mockDeck;
    private Folder mockFolder;
    private CardBulkRequest request;
    private MultipartFile imageFile;
    private MultipartFile audioFile;
    private MultipartFile newImageFile;
    private MultipartFile newAudioFile;
    private CardBulkRequestWrapper wrapper;

    @BeforeEach
    void setUp() {
        mockFolder = mock(Folder.class);
        when(mockFolder.getAccountId()).thenReturn(TestConstants.Account.ACCOUNT_ID);

        mockDeck = mock(Deck.class);
        when(mockDeck.getFolder()).thenReturn(mockFolder);
    }

    // ==================== 헬퍼 메서드 ====================

    private CardBulkRequest createCardRequest(Long cardId, String name, String description) {
        CardBulkRequest request = new CardBulkRequest();
        request.setCardId(cardId);
        request.setDeckId(TestConstants.Deck.DECK_ID);
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
        CardBulkRequestWrapper wrapper = new CardBulkRequestWrapper() {
        };
        wrapper.setRequests(Collections.singletonList(request));
        return wrapper;
    }

    // ==================== 테스트 ====================

    @Test
    @DisplayName("카드 생성 및 첨부파일 검증 -> (성공)")
    void 신규_카드_생성_첨부파일_검즘() throws Exception {
        // Given
        request = createCardRequest(null, TestConstants.Card.CARD_NAMES.get(0), TestConstants.Card.CARD_DESCRIPTIONS.get(0));
        imageFile = mockMultipartFile(TestConstants.File.EMPTY_FLAG, TestConstants.Attachment.IMAGE_MIME_JPEG);
        audioFile = mockMultipartFile(TestConstants.File.EMPTY_FLAG, TestConstants.Attachment.AUDIO_MIME_MPEG);
        request.setImage(imageFile);
        request.setAudio(audioFile);
        wrapper = createWrapper(request);

        when(deckRepository.findByIdAndIsDeletedFalse(TestConstants.Deck.DECK_ID)).thenReturn(Optional.of(mockDeck));

        // When
        cardService.upsert(TestConstants.Account.ACCOUNT_ID, wrapper);

        // When: 카드 저장 및 첨부파일 처리 검증
        ArgumentCaptor<List<Card>> captor = ArgumentCaptor.forClass(List.class);
        verify(cardRepository, times(1)).saveAll(captor.capture());
        List<Card> savedCards = captor.getValue();
        assertEquals(1, savedCards.size());
        Card savedCard = savedCards.get(0);
        assertEquals(TestConstants.Card.CARD_NAMES.get(0), savedCard.getName());
        assertEquals(TestConstants.Card.CARD_DESCRIPTIONS.get(0), savedCard.getDescription());
        assertEquals(mockDeck, savedCard.getDeck());

        // Then: 첨부파일 벌크 처리 검증
        ArgumentCaptor<List<AttachmentBulkData>> attachmentCaptor = ArgumentCaptor.forClass(List.class);
        verify(attachmentService, times(1)).saveAllAttachments(attachmentCaptor.capture());
        List<AttachmentBulkData> capturedAttachments = attachmentCaptor.getValue();
        assertEquals(2, capturedAttachments.size());

        // 이미지 첨부파일 검증
        boolean imageAttachmentExists = capturedAttachments.stream()
                .anyMatch(abd -> abd.getFile() == imageFile &&
                        abd.getFolder().equals(AttachmentFolder.IMAGE.name()) &&
                        abd.getTarget().equals(FileTarget.CARD.name()) &&
                        abd.getCard() == savedCard);
        assertTrue(imageAttachmentExists);

        // 오디오 첨부파일 검증
        boolean audioAttachmentExists = capturedAttachments.stream()
                .anyMatch(abd -> abd.getFile() == audioFile &&
                        abd.getFolder().equals(AttachmentFolder.AUDIO.name()) &&
                        abd.getTarget().equals(FileTarget.CARD.name()) &&
                        abd.getCard() == savedCard);
        assertTrue(audioAttachmentExists);
    }

    @Test
    @DisplayName("기존 카드 수정 및 첨부파일 검증 -> (성공)")
    void 기존_카드_수정_첨부파일_업데이트_검증() throws Exception {
        // Given: 기존 카드 수정 요청
        request = createCardRequest(TestConstants.Card.CARD_ID, TestConstants.Card.CARD_NAMES.get(1), TestConstants.Card.CARD_DESCRIPTIONS.get(1));
        newImageFile = mockMultipartFile(TestConstants.File.EMPTY_FLAG, TestConstants.Attachment.IMAGE_MIME_JPEG);
        newAudioFile = mockMultipartFile(!TestConstants.File.EMPTY_FLAG, TestConstants.Attachment.AUDIO_MIME_MPEG);
        request.setImage(newImageFile);
        request.setAudio(newAudioFile);
        CardBulkRequestWrapper wrapper = createWrapper(request);

        // 기존 카드 및 첨부파일 설정
        Card existingCard = Card.builder()
                .name(TestConstants.Card.CARD_NAMES.get(0))
                .description(TestConstants.Card.CARD_DESCRIPTIONS.get(0))
                .deck(mockDeck)
                .build();
        // 기존에 저장된 이미지, 오디오 첨부파일 설정
        Attachment oldImageAttachment = Attachment.builder().fileKey(TestConstants.Attachment.OLD_IMAGE_KEY).fileType(TestConstants.Attachment.IMAGE_MIME_JPEG).build();
        Attachment oldAudioAttachment = Attachment.builder().fileKey(TestConstants.Attachment.OLD_AUDIO_KEY).fileType(TestConstants.Attachment.AUDIO_MIME_MPEG).build();

        // Entity에서는 Setter를 제공하지 않기 때문에 ReflectionTestUtils로 값 지정
        AttachmentCardTarget imageTarget = new AttachmentCardTarget() {
        };
        ReflectionTestUtils.setField(imageTarget, TestConstants.Attachment.ATTACHMENT_FIELD, oldImageAttachment);

        AttachmentCardTarget audioTarget = new AttachmentCardTarget() {
        };
        ReflectionTestUtils.setField(audioTarget, TestConstants.Attachment.ATTACHMENT_FIELD, oldAudioAttachment);
        ReflectionTestUtils.setField(existingCard, TestConstants.Attachment.ATTACHMENT_CARD_TARGET_FIELD, List.of(imageTarget, audioTarget));

        // repository 스텁 설정: cardId에 대해 기존 카드 반환
        when(cardRepository.findByIdAndIsDeletedFalse(TestConstants.Card.CARD_ID)).thenReturn(Optional.of(existingCard));

        // When: 기존 카드 업데이트 실행
        cardService.upsert(TestConstants.Account.ACCOUNT_ID, wrapper);

        // Then: 카드 정보 업데이트 검증
        assertEquals(TestConstants.Card.CARD_NAMES.get(1), existingCard.getName());
        assertEquals(TestConstants.Card.CARD_DESCRIPTIONS.get(1), existingCard.getDescription());

        // Then: bulk 삭제 검증 - 두 요청 모두 기존 파일 키가 삭제 대상에 포함되어야 함
        ArgumentCaptor<List<String>> deleteCaptor = ArgumentCaptor.forClass(List.class);
        verify(attachmentService, times(1)).deleteAllAttachments(deleteCaptor.capture());
        List<String> deletedKeys = deleteCaptor.getValue();

        assertEquals(2, deletedKeys.size());
        assertTrue(deletedKeys.contains(TestConstants.Attachment.OLD_IMAGE_KEY));
        assertTrue(deletedKeys.contains(TestConstants.Attachment.OLD_AUDIO_KEY));

        // 두 번 호출되도록 검증 (첫 번째: newAttachments, 두 번째: updateAttachments에서 파일이 존재하는 항목)
        ArgumentCaptor<List<AttachmentBulkData>> saveCaptor = ArgumentCaptor.forClass(List.class);
        verify(attachmentService, times(2)).saveAllAttachments(saveCaptor.capture());
        List<List<AttachmentBulkData>> allSaveCalls = saveCaptor.getAllValues();

        // 첫 번째 호출은 newAttachments인데, 이 경우 빈 리스트여야 함
        List<AttachmentBulkData> firstCallAttachments = allSaveCalls.get(0);
        assertTrue(firstCallAttachments.isEmpty(), "첫 번째 호출은 빈 리스트여야 합니다.");

        // 두 번째 호출은 processUpdateAttachments에서 호출되며, 새 이미지 파일만 포함된 리스트여야 함
        List<AttachmentBulkData> secondCallAttachments = allSaveCalls.get(1);
        assertEquals(1, secondCallAttachments.size(), "두 번째 호출에는 이미지 첨부파일 항목만 있어야 합니다.");
        AttachmentBulkData imageBulkData = secondCallAttachments.get(0);

        assertEquals(newImageFile, imageBulkData.getFile());
        assertEquals(AttachmentFolder.IMAGE.name(), imageBulkData.getFolder());
        assertEquals(FileTarget.CARD.name(), imageBulkData.getTarget());
        assertEquals(existingCard, imageBulkData.getCard());
        assertEquals(TestConstants.Attachment.OLD_IMAGE_KEY, imageBulkData.getOldFileKey());
    }
}
