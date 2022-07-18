package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.login.oauth.common.AuthResponse;
import com.mpnp.baechelin.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal User user) {
        return null;
    }
}
