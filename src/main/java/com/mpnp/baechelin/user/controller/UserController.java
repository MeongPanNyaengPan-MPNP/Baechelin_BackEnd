package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.user.service.UserService;
import com.mpnp.baechelin.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, "refresh_token");
        return new ResponseEntity<>("로그아웃 완료", HttpStatus.OK);
    }
}
