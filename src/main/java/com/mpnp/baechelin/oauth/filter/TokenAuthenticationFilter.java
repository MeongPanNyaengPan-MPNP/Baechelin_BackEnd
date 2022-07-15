package com.mpnp.baechelin.oauth.filter;

import com.mpnp.baechelin.oauth.exception.ErrorCode;
import com.mpnp.baechelin.oauth.token.AuthToken;
import com.mpnp.baechelin.oauth.token.AuthTokenProvider;
import com.mpnp.baechelin.util.HeaderUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
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

        try {
            if (token != null && token.tokenValidate()) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                // SecurityContextHolder 에 인증 객체를 넣는다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (SignatureException e) {
            log.info("잘못된 JWT 서명입니다.");
            request.setAttribute("exception", ErrorCode.WRONG_TYPE_SIGNATURE.getCode());
        } catch (MalformedJwtException e) {
            log.info("유효하지 않은 구성의 JWT 토큰입니다.");
            request.setAttribute("exception", ErrorCode.WRONG_TYPE_TOKEN.getCode());
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.getCode());
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 형식이나 구성의 JWT 토큰입니다.");
            request.setAttribute("exception", ErrorCode.WRONG_TYPE_TOKEN.getCode());
        } catch (IllegalArgumentException e) {
            log.info(e.toString().split(":")[1].trim());
            request.setAttribute("exception", ErrorCode.TOKEN_NOT_EXIST.getCode());
        }

        filterChain.doFilter(request, response);
    }
}
