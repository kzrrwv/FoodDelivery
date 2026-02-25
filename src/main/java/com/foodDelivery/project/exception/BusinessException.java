package com.foodDelivery.project.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException{

    private HttpStatus httpStatus;

    public BusinessException(String message, HttpStatus httpStatus) {
        super(" Возникла ошибка: " + message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
