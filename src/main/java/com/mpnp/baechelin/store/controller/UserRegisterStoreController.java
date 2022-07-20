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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

        /*
         * 이미지 파일 리스트 유효성 체크
         * @Valid 어노테이션은 Collection 에는 적용할 수 없으므로 직접 유효성 체크를 해준다.
         */
        List<MultipartFile> userRegisterStoreImageList = userRegisterStoreRequestDto.getUserRegisterStoreImageList();
        for (MultipartFile multipartFile : userRegisterStoreImageList) {
            if (multipartFile.isEmpty()) {
                throw new NullPointerException();
            }
        }

        userRegisterStoreService.registerStore(userRegisterStoreRequestDto, userDetails.getUsername());
        return new ResponseEntity<>("업장 등록 성공", HttpStatus.OK);
    }


}
