package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.oauth.common.AuthResponse;
import com.mpnp.baechelin.user.entity.user.User;
import com.mpnp.baechelin.user.service.UserService;
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

    @GetMapping("/oauth/redirect")
    public AuthResponse<String> loginTest(@RequestParam String token) {
        return AuthResponse.success("access_token", token);
    }

    @GetMapping
    public AuthResponse<User> getUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User user = userService.getUser(principal.getUsername());

        return AuthResponse.success("user", user);
    }
}
