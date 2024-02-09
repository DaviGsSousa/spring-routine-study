package com.study.internal.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class ErrorHandler {
//    @ExceptionHandler(NullPointerException.class) // exception handled
//    public ResponseEntity<ErrorResponse> handleNullPointerExceptions(Exception e) {
//
//        HttpStatus status = HttpStatus.NOT_FOUND; // 404
//        return new ResponseEntity<>(new ErrorResponse(status, "Endpoint não encontrado"), status);
//    }

    @ExceptionHandler(DataIntegrityViolationException.class) // exception handled
    public ResponseEntity<ErrorResponse> dataIntegrityViolationExceptions(Exception e) {

        String cause = "";
        HttpStatus status = HttpStatus.BAD_REQUEST; // 404

        if (e.getMessage().contains("uindex") || e.getMessage().contains("constraint")) {
            cause = "Esse item já foi cadastrado anteriormente";
        }

        return new ResponseEntity<>(new ErrorResponse(status, cause), status);
    }

    // fallback method
    @ExceptionHandler(Exception.class) // exception handled
    public ResponseEntity<ErrorResponse> handleExceptions(Exception e) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorResponse(status, "Erro interno no servidor", e.getMessage()), status);
    }
}