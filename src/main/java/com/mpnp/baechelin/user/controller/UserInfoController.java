package com.mpnp.baechelin.user.controller;

import com.mpnp.baechelin.user.dto.UserInfoResponseDto;
import com.mpnp.baechelin.user.entity.user.User;
import com.mpnp.baechelin.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userinfo")
@RequiredArgsConstructor
@Slf4j
public class UserInfoController {
    private final UserInfoService userInfoService;

    @GetMapping
    // Security 추가 후 변경할 예정
//    public ResponseEntity<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal User user) {
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@RequestParam Integer userId) {
        log.warn("USERID {} " , userId);
//        return new ResponseEntity<>(userInfoService.giveUserInfo(user), HttpStatus.OK);
        return new ResponseEntity<>(userInfoService.giveUserInfo(userId), HttpStatus.OK);
    }
}
