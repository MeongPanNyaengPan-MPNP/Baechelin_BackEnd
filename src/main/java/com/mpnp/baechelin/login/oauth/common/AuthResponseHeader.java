package com.mpnp.baechelin.login.oauth.common;

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
