package com.mpnp.baechelin.util;

import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

// 쿠키를 생성하고 반환해주는 클래스
public class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        // 쿠키가 있을 때
      if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return Optional.of(cookie);
                }
            }
        }
      return Optional.empty();
    }

    // 쿠키 생성
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .domain(".bae-chelin.com")
                .path("/")
                .httpOnly(true)
                .maxAge(maxAge)
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
//        Cookie cookie = new Cookie(name, value);
//
//        cookie.setPath("/");
//        cookie.setHttpOnly(true); // XSS 공격을 막기 위한 설정
//        cookie.setMaxAge(maxAge);
//        cookie.setDomain(".bae-chelin.com");
//
//        response.addCookie(cookie);
    }

    // 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    ResponseCookie deleteCookie = ResponseCookie.from(name, null)
                            .domain(".bae-chelin.com")
                            .path("/")
                            .httpOnly(true)
                            .maxAge(0)
                            .secure(true)
                            .sameSite("None")
                            .build();

//                    cookie.setValue("");
//                    cookie.setPath("/");
//                    cookie.setMaxAge(0);

                    response.addHeader("Set-Cookie", cookie.toString());
                }
            }
        }
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}
