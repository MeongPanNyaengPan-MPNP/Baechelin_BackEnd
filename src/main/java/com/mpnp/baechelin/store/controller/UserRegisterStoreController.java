package com.mpnp.baechelin.store.controller;

import com.mpnp.baechelin.store.dto.userRegisterStore.UserRegisterStoreRequestDto;
import com.mpnp.baechelin.store.service.UserRegisterStoreService;
import com.mpnp.baechelin.user.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class UserRegisterStoreController {

    private final UserRegisterStoreService userRegisterStoreService;

    /**
     * 유저 업장 등록
     * @param userRegisterStoreRequestDto
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerStore(
            @ModelAttribute UserRegisterStoreRequestDto userRegisterStoreRequestDto) {
        userRegisterStoreService.registerStore(userRegisterStoreRequestDto);
        return new ResponseEntity<>("업장 등록 성공", HttpStatus.OK);
    }
}
