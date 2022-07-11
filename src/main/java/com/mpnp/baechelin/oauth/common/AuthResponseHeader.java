package com.mpnp.baechelin.oauth.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseHeader {
    private int code;
    private String message;
}
