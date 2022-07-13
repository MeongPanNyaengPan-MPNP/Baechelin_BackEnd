package com.mpnp.baechelin.oauth.controller;

import com.mpnp.baechelin.config.properties.AppProperties;
import com.mpnp.baechelin.oauth.common.AuthResponse;
import com.mpnp.baechelin.oauth.entity.RoleType;
import com.mpnp.baechelin.oauth.service.AuthService;
import com.mpnp.baechelin.oauth.token.AuthToken;
import com.mpnp.baechelin.oauth.token.AuthTokenProvider;
import com.mpnp.baechelin.user.entity.user.UserRefreshToken;
import com.mpnp.baechelin.user.repository.UserRefreshTokenRepository;
import com.mpnp.baechelin.util.CookieUtil;
import com.mpnp.baechelin.util.HeaderUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Date;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "Access Token 만료 시 Refresh Token을 이용하여 재발급 받는 메소드")
    @GetMapping("/refresh")
    public AuthResponse refreshToken (HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }
}
