package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;


}
