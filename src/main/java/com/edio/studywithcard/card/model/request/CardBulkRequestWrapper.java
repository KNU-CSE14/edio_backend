package com.edio.studywithcard.card.model.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class CardBulkRequestWrapper {
    private List<CardBulkRequest> requests;
}
