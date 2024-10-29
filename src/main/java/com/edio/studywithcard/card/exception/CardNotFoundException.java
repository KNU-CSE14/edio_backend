package com.edio.studywithcard.card.exception;

import com.edio.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CardNotFoundException extends BaseException {
    public CardNotFoundException(long id){
        super(HttpStatus.NOT_FOUND, String.format("Card with ID %d not found.", id));
    }
}
