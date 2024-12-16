package com.edio.studywithcard.deck.service;

import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckUpdateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DeckService {
    // 덱 생성
    DeckResponse createDeck(DeckCreateRequest deckCreateRequest, MultipartFile file);

    // 덱 조회
    DeckResponse getDeck(Long id);

    // 덱 수정
    void updateDeck(Long id, DeckUpdateRequest deckUpdateRequest);

    // 덱 이동
    void moveDeck(Long id, Long newFolderId);

    // 덱 삭제
    void deleteDeck(Long id);
}
