package com.mpnp.baechelin.user.service;

import com.mpnp.baechelin.login.jwt.repository.UserRefreshTokenRepository;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.dto.UserResponseDto;
import com.mpnp.baechelin.user.repository.UserRepository;
import com.mpnp.baechelin.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public void logout(HttpServletRequest request, HttpServletResponse response, String socialId) {
        userRefreshTokenRepository.deleteBySocialId(socialId);
        CookieUtil.deleteCookie(request, response, "refresh_token");
    }

    public UserResponseDto getUserInfo(String socialId) {
        User targetUser = userRepository.findBySocialId(socialId);
        return new UserResponseDto(targetUser);
    }
}

