package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.oauth.common.AuthResponse;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @ApiOperation(value = "토큰 테스트용 메소드 (사용하지 않음)")
    @GetMapping("/oauth/redirect")
    public AuthResponse<String> loginTest(@RequestParam(required = false) String token) {
        return AuthResponse.success("access_token", token);
    }

    @ApiOperation(value = "유저 정보 가져오기 테스트용 메소드 (사용하지 않음)")
    @GetMapping
    public AuthResponse<User> getUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User user = userService.getUser(principal.getUsername());

        return AuthResponse.success("user", user);
    }
}
