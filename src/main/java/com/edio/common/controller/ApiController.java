package com.edio.common.controller;

import com.edio.common.exception.BaseException;
import com.edio.common.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ApiController {

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleBaseException(BaseException e){
        return new ErrorResponse(e.isSuccess(), e.getDetailMessage());
    }


}
