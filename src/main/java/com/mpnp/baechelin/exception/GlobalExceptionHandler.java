package com.mpnp.baechelin.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = SignatureException.class)
    protected ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e) {
        log.error("잘못된 JWT 서명입니다.");
        return ErrorResponse.toResponseEntity(ErrorCode.WRONG_TYPE_SIGNATURE);
    }

    @ExceptionHandler(value = MalformedJwtException.class)
    protected ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException e) {
        log.error("유효하지 않은 구성의 JWT 토큰입니다.");
        return ErrorResponse.toResponseEntity(ErrorCode.WRONG_TYPE_TOKEN);
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    protected ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("만료된 JWT 토큰입니다.");
        return ErrorResponse.toResponseEntity(ErrorCode.EXPIRED_TOKEN);
    }

    @ExceptionHandler(value = UnsupportedJwtException.class)
    protected ResponseEntity<ErrorResponse> handleUnsupportedJwtException(UnsupportedJwtException e) {
        log.error("지원되지 않는 형식이나 구성의 JWT 토큰입니다.");
        return ErrorResponse.toResponseEntity(ErrorCode.WRONG_TYPE_TOKEN);
    }

    @ExceptionHandler(value = RestClientException.class)
    protected ResponseEntity<ErrorResponse> handleUnsupportedJwtException(RestClientException e) {
        log.error("지원되지 않는 형식이나 구성의 JWT 토큰입니다.");
        return ErrorResponse.toResponseEntity(ErrorCode.API_LOAD_FAILURE);
    }
}
