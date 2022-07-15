package com.mpnp.baechelin.oauth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SUCCESS_MESSAGE(200, "SUCCESS"),
    NOT_FOUND_MESSAGE(500, "NOT FOUND"),
    FAILED_MESSAGE(500, "서버에서 오류가 발생하였습니다."),
    INVALID_ACCESS_TOKEN(401, "유효하지 않은 Access Token입니다."),
    INVALID_REFRESH_TOKEN(401, "유효하지 않은 Refresh Token입니다."),
    NOT_EXPIRED_TOKEN_YET(401,"만료되지 않은 JWT 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 JWT 토큰입니다."),
    WRONG_TYPE_TOKEN(401, "잘못된 JWT 토큰입니다."),
    ACCESS_DENIED(401, "접근이 거부되었습니다."),
    ALREADY_LOGIN_ACCOUNT(400, "ALREADY_LOGIN_ACCOUNT");

    private final int code;
    private final String message;
}
