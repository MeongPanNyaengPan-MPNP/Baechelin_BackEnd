package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.user.dto.UserInfoResponseDto;
import com.mpnp.baechelin.user.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userinfo")
@RequiredArgsConstructor
@Slf4j
public class UserInfoController {
    private final UserInfoService userInfoService;

    @GetMapping
    @ApiOperation(value = "유저 정보를 반환합니다")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(userInfoService.giveUserInfo(user.getUsername()), HttpStatus.OK);
    }
}
