package com.sni.secure_chat.exceptions.advice;

import com.sni.secure_chat.exceptions.ErrorMessage;
import com.sni.secure_chat.exceptions.HttpException;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@CommonsLog
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.info("A bad request", ex);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ErrorMessage> handleHttpException(HttpException e){
        log.info("Client error, check message in stacktrace for details", e);
        return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception e){
        log.error("An error occurred", e);
        return ResponseEntity.status(500).body(new ErrorMessage("An error occurred"));
    }

}
