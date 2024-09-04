package com.edio.studywithcard.card.exception;

import com.edio.common.exception.BaseException;

public class CardNotFoundException extends BaseException {
    public CardNotFoundException(long id){
        super(String.format("Card with ID %d not found.", id));
    }
}
