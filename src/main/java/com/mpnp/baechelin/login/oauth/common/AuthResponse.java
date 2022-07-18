package com.mpnp.baechelin.login.oauth.common;

import com.mpnp.baechelin.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class AuthResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status = 200;
    private final String code = "S-SUC200";
    private final String result = "SUCCESS";
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
