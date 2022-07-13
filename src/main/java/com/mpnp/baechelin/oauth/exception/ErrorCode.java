package com.mpnp.baechelin.oauth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SUCCESS_MESSAGE(200, "SUCCESS"),
    NOT_FOUND_MESSAGE(500, "NOT FOUND"),
    FAILED_MESSAGE(500, "서버에서 오류가 발생하였습니다."),
    INVALID_ACCESS_TOKEN(400, "Invalid access token."),
    INVALID_REFRESH_TOKEN(400, "Invalid refresh token."),
    NOT_EXPIRED_TOKEN_YET(400,"Not expired token yet."),
    ALREADY_LOGIN_ACCOUNT(400, "ALREADY_LOGIN_ACCOUNT");

    private final int code;
    private final String message;
}
