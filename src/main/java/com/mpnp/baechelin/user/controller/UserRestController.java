package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @RequestMapping("/login")
    public String kakaoLogin(@RequestParam String code) {
        String accessToken = userService.getKakaoToken(code);
        System.out.println("accessToke : " + accessToken);
        return accessToken;
    }
}
