package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.service.UserService;
import com.mpnp.baechelin.util.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    private final OAuthService oAuthService;

    @RequestMapping("")
    public String getTokenTest(@RequestParam String code) {
        System.out.println("#######" + code);

        String access_token =oAuthService.getKakaoAccessToken(code);
        System.out.println("###access_token" + access_token);

        return "index";
    }
}
