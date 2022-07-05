package com.mpnp.baechelin.util;

import javax.servlet.http.HttpServletRequest;

// 헤더에 포함되어 보내지는 access token을 뽑아주는 클래스
public class HeaderUtil {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer "; // Bearer 다음에 스페이스바 한번 잊지 말기

    public static String getAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_AUTHORIZATION);

        if (headerValue == null) {
            return null;
        }

        if (headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
