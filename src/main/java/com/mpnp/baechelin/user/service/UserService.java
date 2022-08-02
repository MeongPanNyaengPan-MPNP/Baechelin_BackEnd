package com.mpnp.baechelin.user.service;

import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.login.jwt.AuthToken;
import com.mpnp.baechelin.login.jwt.AuthTokenProvider;
import com.mpnp.baechelin.login.jwt.repository.UserRefreshTokenRepository;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.dto.UserResponseDto;
import com.mpnp.baechelin.user.repository.UserRepository;
import com.mpnp.baechelin.util.CookieUtil;
import com.mpnp.baechelin.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final AuthTokenProvider tokenProvider;

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, "refresh_token")
                .map(Cookie::getValue)
                .orElse((null));

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_EXIST);
        }

        // Cookie에 담겨있는 refresh token 삭제
        CookieUtil.deleteCookie(request, response, "refresh_token");

        // DB에 저장되어 있는 refresh token 삭제
        userRefreshTokenRepository.deleteByRefreshToken(refreshToken);
    }
    @Cacheable(value="user", key="#socialId", cacheManager = "cacheManager")
    public UserResponseDto getUserInfo(String socialId) {
        User targetUser = userRepository.findBySocialId(socialId);
        return new UserResponseDto(targetUser);
    }
}

