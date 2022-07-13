package com.mpnp.baechelin.store.controller;

import com.mpnp.baechelin.store.dto.userRegisterStore.UserRegisterStoreRequestDto;
import com.mpnp.baechelin.store.service.UserRegisterStoreService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class UserRegisterStoreController {

    private final UserRegisterStoreService userRegisterStoreService;

    @ApiOperation(value = "유저가 배리어프리 업장을 등록하는 메소드")
    @PostMapping("/register")
    public ResponseEntity<String> registerStore(
            @ModelAttribute UserRegisterStoreRequestDto userRegisterStoreRequestDto,
            @AuthenticationPrincipal User userDetails) {

        userRegisterStoreService.registerStore(userRegisterStoreRequestDto, userDetails.getUsername());
        return new ResponseEntity<>("업장 등록 성공", HttpStatus.OK);
    }


}
