package com.mpnp.baechelin.oauth.filter;

import com.mpnp.baechelin.oauth.token.AuthToken;
import com.mpnp.baechelin.oauth.token.AuthTokenProvider;
import com.mpnp.baechelin.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/* 토큰을 파싱하여 유저 정보를 가지고 오고, 토큰이 유효할 경우 그 유저에게 권한을 부여하는 클래스
* usernamepasswordAuthenticationFilter 보다 먼저 토큰을 바탕으로 유저를 등록시키는 클래스
* OncePerRequestFilter는 요청 당 한번 만 일어나도록 하는 추상클래스이다.
*/
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 요청값의 header에서 토큰을 뽑아온다.
        String tokenStr = HeaderUtil.getAccessToken(request);
        // String으로 된 token을 AuthToken객체로 변환해준다.
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);

        if (token.validate()) {
            // 토큰이 유효하다면 인증 객체 생성
            Authentication authentication = tokenProvider.getAuthentication(token);
            // SecurityContextHolder 에 인증 객체를 넣는다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
