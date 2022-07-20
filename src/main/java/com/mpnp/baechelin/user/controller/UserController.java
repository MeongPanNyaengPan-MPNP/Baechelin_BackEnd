package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.common.SuccessResponse;
import com.mpnp.baechelin.user.service.UserService;
import com.mpnp.baechelin.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping("/logout")
    public SuccessResponse logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal User user
            ) {
        userService.logout(request, response, user.getUsername());

        return new SuccessResponse("로그아웃");
    }
}
