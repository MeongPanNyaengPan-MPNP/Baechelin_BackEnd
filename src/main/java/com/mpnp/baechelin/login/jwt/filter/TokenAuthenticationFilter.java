package com.mpnp.baechelin.login.jwt.filter;

import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.login.jwt.AuthToken;
import com.mpnp.baechelin.login.jwt.AuthTokenProvider;
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
        AuthToken token = tokenProvider.convertAccessToken(request);

        try {
            if (token != null && token.tokenValidate()) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                // SecurityContextHolder 에 인증 객체를 넣는다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_EXIST);
            }
        // 에러가 발생했을 때, request에 attribute를 세팅하고 RestAuthenticationEntryPoint로 request를 넘겨준다.
        } catch (SignatureException e) {
            log.info("잘못된 JWT 서명입니다.");
            request.setAttribute("exception", ErrorCode.WRONG_TYPE_SIGNATURE.getCode());
        } catch (MalformedJwtException e) {
            log.info("유효하지 않은 구성의 JWT 토큰입니다.");
            request.setAttribute("exception", ErrorCode.WRONG_TYPE_TOKEN.getCode());
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            request.setAttribute("exception", ErrorCode.EXPIRED_ACCESS_TOKEN.getCode());
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 형식이나 구성의 JWT 토큰입니다.");
            request.setAttribute("exception", ErrorCode.WRONG_TYPE_TOKEN.getCode());
        } catch (IllegalArgumentException e) {
            log.info(e.toString().split(":")[1].trim());
            request.setAttribute("exception", ErrorCode.INVALID_ACCESS_TOKEN.getCode());
        } catch (CustomException e) {
            log.info("Access Token이 존재하지 않습니다.");
            request.setAttribute("exception", ErrorCode.ACCESS_TOKEN_NOT_EXIST.getCode());
        }

        filterChain.doFilter(request, response);
    }
}
