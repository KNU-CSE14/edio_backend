package com.edio.common.util;

import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.card.model.request.CardBulkRequest;
import com.edio.studywithcard.card.model.request.CardBulkRequestWrapper;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.folder.domain.Folder;

import java.util.Collections;

import static com.edio.common.TestConstants.Deck.DECK_ID;
import static com.edio.common.TestConstants.User.ACCOUNT_ID;

public class TestDataUtil {

    private TestDataUtil() {
    }

    public static Folder createFolder(Long id, String name, Folder folder) {
        return Folder.builder()
                .id(id)
                .accountId(ACCOUNT_ID)
                .name(name)
                .parentFolder(folder)
                .build();
    }

    public static Deck createDeck(Folder folder, Category category, String name, String description) {
        return Deck.builder()
                .folder(folder)
                .category(category)
                .name(name)
                .description(description)
                .isShared(false)
                .isFavorite(false)
                .build();
    }

    public static Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .build();
    }

    public static Attachment createAttachment(String fileName, String filePath, String fileKey, Long fileSize, String fileType, String fileTarget) {
        return Attachment.builder()
                .fileName(fileName)
                .filePath(filePath)
                .fileKey(fileKey)
                .fileSize(fileSize)
                .fileType(fileType)
                .fileTarget(fileTarget)
                .build();
    }

    public static CardBulkRequest createCardRequest(Long cardId, String name, String description) {
        CardBulkRequest request = new CardBulkRequest();
        request.setCardId(cardId);
        request.setDeckId(DECK_ID);
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    public static CardBulkRequestWrapper createWrapper(CardBulkRequest request) {
        CardBulkRequestWrapper wrapper = new CardBulkRequestWrapper() {
        };
        wrapper.setRequests(Collections.singletonList(request));
        return wrapper;
    }
}
