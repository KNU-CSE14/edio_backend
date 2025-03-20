package com.edio.common.util;

import com.edio.studywithcard.card.model.request.CardBulkRequest;
import com.edio.studywithcard.card.model.request.CardBulkRequestWrapper;
import com.edio.studywithcard.folder.domain.Folder;

import java.util.Collections;

import static com.edio.common.TestConstants.User.ACCOUNT_ID;
import static com.edio.common.TestConstants.Deck.DECK_ID;

public class TestDataUtil {

    private TestDataUtil() { }

    public static Folder createFolder(Long id, String name) {
        return Folder.builder()
                .id(id)
                .accountId(ACCOUNT_ID)
                .name(name)
                .parentFolder(null)
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
        CardBulkRequestWrapper wrapper = new CardBulkRequestWrapper(){};
        wrapper.setRequests(Collections.singletonList(request));
        return wrapper;
    }
}
