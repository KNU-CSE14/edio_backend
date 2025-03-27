package com.edio.studywithcard.deck.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeckApiUrls {
    public final static String DECK_URL = "/api/deck";
    public final static String DECK_DELETE_URL = "/api/deck/{id}";
    public final static String DECK_POSITION_URL = "/api/deck/position";
}
