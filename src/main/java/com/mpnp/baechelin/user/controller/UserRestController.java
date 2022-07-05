package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping("/oauth/redirect")
    public String loginTest(@RequestParam String token) {
        return token;
    }
}
