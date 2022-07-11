package com.mpnp.baechelin.oauth.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class AuthResponse<T> {
    private final static int SUCCESS = 200;
    private final static int NOT_FOUND = 400;
    private final static int FAILED = 500;
    private final static String SUCCESS_MESSAGE = "SUCCESS";
    private final static String NOT_FOUND_MESSAGE = "NOT FOUND";
    private final static String FAILED_MESSAGE = "서버에서 오류가 발생하였습니다.";
    private final static String INVALID_ACCESS_TOKEN = "Invalid access token.";
    private final static String INVALID_REFRESH_TOKEN = "Invalid refresh token.";
    private final static String NOT_EXPIRED_TOKEN_YET = "Not expired token yet.";

    private final AuthResponseHeader header;
    private final Map<String, T> body;

    public static <T> AuthResponse<T> success(String name, T body) {
        Map<String, T> map = new HashMap<>();
        map.put(name, body);

        return new AuthResponse<>(new AuthResponseHeader(SUCCESS, SUCCESS_MESSAGE), map);
    }

    public static <T> AuthResponse<T> fail() {
        return new AuthResponse<>(new AuthResponseHeader(FAILED, FAILED_MESSAGE), null);
    }

    public static <T> AuthResponse<T> invalidAccessToken() {
        return new AuthResponse<>(new AuthResponseHeader(FAILED, INVALID_ACCESS_TOKEN), null);
    }

    public static <T> AuthResponse<T> invalidRefreshToken() {
        return new AuthResponse<>(new AuthResponseHeader(FAILED, INVALID_REFRESH_TOKEN), null);
    }

    public static <T> AuthResponse<T> notExpiredTokenYet() {
        return new AuthResponse<>(new AuthResponseHeader(FAILED, NOT_EXPIRED_TOKEN_YET), null);
    }
}
