package com.mpnp.baechelin.oauth.common;

import com.mpnp.baechelin.oauth.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseHeader {
    private String code;
    private String message;
}
