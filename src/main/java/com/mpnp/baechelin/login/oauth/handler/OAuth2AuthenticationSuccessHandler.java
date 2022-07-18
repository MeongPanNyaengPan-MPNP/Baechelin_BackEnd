package com.mpnp.baechelin.login.oauth.handler;

import com.mpnp.baechelin.common.properties.AppProperties;
import com.mpnp.baechelin.login.oauth.entity.ProviderType;
import com.mpnp.baechelin.login.oauth.entity.RoleType;
import com.mpnp.baechelin.login.oauth.info.OAuth2UserInfo;
import com.mpnp.baechelin.login.oauth.info.OAuth2UserInfoFactory;
import com.mpnp.baechelin.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.mpnp.baechelin.login.jwt.AuthToken;
import com.mpnp.baechelin.login.jwt.AuthTokenProvider;
import com.mpnp.baechelin.login.jwt.entity.UserRefreshToken;
import com.mpnp.baechelin.login.jwt.repository.UserRefreshTokenRepository;
import com.mpnp.baechelin.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static com.mpnp.baechelin.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.mpnp.baechelin.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN;

// 로그인 성공 시 처리해주는 클래스
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("이미 보내진(커밋된) 응답이므로 " + targetUrl + "으로 리다이렉트 할 수 없습니다.");
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 쿠키에서 redirectUrl을 꺼내온다.
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() &&!isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("승인되지 않은 Redirect URI가 있어 인증을 진행할 수 없습니다.");
        }

        // redirect 할 uri
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        // google or naver or kakao 를 토큰에서 뽑아온다.
        ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

        OidcUser user = (OidcUser) authentication.getPrincipal();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        Collection<? extends GrantedAuthority> authorities = ((OidcUser) authentication.getPrincipal()).getAuthorities();

        RoleType roleType = hasAuthority(authorities, RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;

        Date now = new Date();

        // 엑세스 토큰 생성
        AuthToken accessToken = tokenProvider.createAuthToken(
                userInfo.getId(),
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );


        // refresh 토큰 설정
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // refresh token을 DB에 저장하기 위해 DB에서 refresh token을 불러온다.
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findBySocialId(userInfo.getId());

        // refresh token이 이미 존재한다면 토큰 업데이트
        if (userRefreshToken != null) {
            userRefreshToken.setRefreshToken(refreshToken.getToken());
            userRefreshTokenRepository.saveAndFlush(userRefreshToken);
        } else {
            // refresh token이 없다면 DB에 저장
            userRefreshToken = new UserRefreshToken(userInfo.getId(), refreshToken.getToken());
            userRefreshTokenRepository.saveAndFlush(userRefreshToken);
        }

        // 쿠키 만료시간 = 리프레시 토큰 만료시간
        int cookieMaxAge = (int) refreshTokenExpiry / 60;

        // 기존에 cookie에 들어있는 refresh token을 삭제하고 다시 저장한다.
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        // URL로 토큰 전송
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", accessToken.getToken())
                .build().toUriString();
    }

    /*
    * Spring Security에서 로그인하는 과정에서 로그인이 실패한 경우 자동으로 세션에 관련 에러를 저장한다.
    * 만약 로그인이 실패한 상황이 발생하는 경우 이 에러가 세션에 저장되는데, 이 상태에서 다시 로그인이 성공했다면 세션에 있는 에러를 삭제해야한다.
    * clearAuthenticationAttributes 는 세션에 있는 에러를 삭제하는 메소드이다.
    * */
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }


    // 옮은 인가인지 검사하는 로직
    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        if (authorities == null) {
            return false;
        }

        for(GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    // 받아온 uri가 옳은 uri인지 검사하는 로직
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
