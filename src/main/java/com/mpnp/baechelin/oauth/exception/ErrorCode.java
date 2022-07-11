package com.mpnp.baechelin.oauth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ALREADY_LOGIN_ACCOUNT("ALREADY_LOGIN_ACCOUNT");

    private final String message;
}
