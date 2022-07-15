package com.mpnp.baechelin.oauth.common;

import com.mpnp.baechelin.oauth.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class AuthResponse<T> {

    private final AuthResponseHeader header;
    private final Map<String, T> body;

    public static <T> AuthResponse<T> success(String name, T body) {
        Map<String, T> map = new HashMap<>();
        map.put(name, body);

        return new AuthResponse<>(new AuthResponseHeader(
                ErrorCode.SUCCESS_MESSAGE.getCode(),
                ErrorCode.SUCCESS_MESSAGE.getMessage()), map);
    }

    // 이 밑에 코드는 전부 exception 으로 빼기
    public static <T> AuthResponse<T> fail() {
        return new AuthResponse<>(new AuthResponseHeader(
                ErrorCode.FAILED_MESSAGE.getCode(),
                ErrorCode.FAILED_MESSAGE.getMessage()), null);
    }

    public static <T> AuthResponse<T> invalidAccessToken() {
        return new AuthResponse<>(new AuthResponseHeader(
                ErrorCode.INVALID_ACCESS_TOKEN.getCode(),
                ErrorCode.INVALID_ACCESS_TOKEN.getMessage()), null);
    }

    public static <T> AuthResponse<T> invalidRefreshToken() {
        return new AuthResponse<>(new AuthResponseHeader(
                ErrorCode.INVALID_REFRESH_TOKEN.getCode(),
                ErrorCode.INVALID_REFRESH_TOKEN.getMessage()), null);
    }

    public static <T> AuthResponse<T> notExpiredTokenYet() {
        return new AuthResponse<>(new AuthResponseHeader(
                ErrorCode.NOT_EXPIRED_TOKEN_YET.getCode(),
                ErrorCode.NOT_EXPIRED_TOKEN_YET.getMessage()), null);
    }
}
