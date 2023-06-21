package com.example.ratelimiter.controller;

import com.example.ratelimiter.exception.BadRequestException;
import com.example.ratelimiter.exception.NotFoundException;
import com.example.ratelimiter.model.ErrorMessage;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ErrorMessage > handleNotFoundException(RuntimeException ex) {

        return new ResponseEntity<>(ErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<ErrorMessage> handleBadRequestException(RuntimeException ex) {

        return new ResponseEntity<>(ErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build(),
                HttpStatus.BAD_REQUEST);
    }

}
