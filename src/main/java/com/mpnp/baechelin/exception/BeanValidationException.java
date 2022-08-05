package com.mpnp.baechelin.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class BeanValidationException {
    // Bean Validation ExceptionHandler
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> validExceptionNotControlled(ConstraintViolationException cve) {
        return ErrorResponse.constraintMsgToResponseEntity(ErrorCode.WRONG_INPUT, cve.getConstraintViolations().iterator().next().getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException mae) {
        return ErrorResponse.constraintMsgToResponseEntity
                (ErrorCode.WRONG_INPUT, mae.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }
}
