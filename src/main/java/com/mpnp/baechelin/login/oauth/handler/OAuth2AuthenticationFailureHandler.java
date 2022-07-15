package com.mpnp.baechelin.login.oauth.handler;

import com.mpnp.baechelin.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.mpnp.baechelin.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    /* 로그인 실패 시 처리 커스터마이징
    *  로그인을 실패했을 경우, 쿠키에 저장한 리프레시 토큰을 삭제하고, 에러 uri로 리다이렉트 시켜야한다.
    * */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        // 리프레시 토큰을 저장한 쿠키에 들어있는 redirect uri를 가져온다.
        String targetUrl = CookieUtil.getCookie(request, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("/");

        exception.printStackTrace(); // 에러 로그 모두 출력

        // 기존의 url을 에러 메세지가 담긴 url로 변경
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        // 쿠키 삭제
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        // 에러 메세지가 담긴 url로 리다이렉트 시킨다.
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
