package com.minhnhat.demo.common.handler;

import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<ErrorResponse> handleLogicException(LogicException ex) {

        ErrorResponse response = new ErrorResponse(
                ex.getServiceName(),   // service
                ex.getMessage(),       // message
                ex.getMessageId()      // code
        );

        return ResponseEntity.badRequest().body(response);
    }

    // fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

        ErrorResponse response = new ErrorResponse(
                "SYSTEM",
                "Internal Server Error",
                "SYS_500"
        );

        return ResponseEntity.internalServerError().body(response);
    }
}
