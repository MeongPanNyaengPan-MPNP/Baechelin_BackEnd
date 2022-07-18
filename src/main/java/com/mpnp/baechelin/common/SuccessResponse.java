package com.mpnp.baechelin.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SuccessResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final HttpStatus status = HttpStatus.OK;
    private final String code = "S-SUC200";
    private final String result = "SUCCESS";
    private String message;

    public SuccessResponse(String message) {
        this.message = message;
    }
}
