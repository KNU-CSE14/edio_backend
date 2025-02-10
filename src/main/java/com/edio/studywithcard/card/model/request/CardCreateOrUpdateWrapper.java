package com.edio.studywithcard.card.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CardCreateOrUpdateWrapper {
    private List<CardCreateOrUpdateRequest> request;
}
