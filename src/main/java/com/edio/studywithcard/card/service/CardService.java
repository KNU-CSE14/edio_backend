package com.edio.studywithcard.card.service;

import com.edio.studywithcard.card.model.request.CardCreateRequest;
import com.edio.studywithcard.card.model.request.CardDeleteRequest;
import com.edio.studywithcard.card.model.request.CardUpdateRequest;
import com.edio.studywithcard.card.model.response.CardResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CardService {
    CardResponse createCard(CardCreateRequest request, MultipartFile[] files);

    void updateCard(CardUpdateRequest request, MultipartFile[] files);

    void deleteCard(CardDeleteRequest request);
}
