package com.edio.studywithcard.deck.service;

import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckDeleteRequest;
import com.edio.studywithcard.deck.model.request.DeckMoveRequest;
import com.edio.studywithcard.deck.model.request.DeckUpdateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DeckService {
    // 덱 조회
    DeckResponse getDeck(Long id);

    // 덱 생성
    DeckResponse createDeck(DeckCreateRequest request, MultipartFile file);

    // 덱 수정
    void updateDeck(DeckUpdateRequest request, MultipartFile file);

    // 덱 이동
    void moveDeck(DeckMoveRequest request);

    // 덱 삭제
    void deleteDeck(DeckDeleteRequest request);
}
