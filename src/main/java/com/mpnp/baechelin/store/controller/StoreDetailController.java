package com.mpnp.baechelin.store.controller;

import com.mpnp.baechelin.store.dto.StoreResponseDto;
import com.mpnp.baechelin.store.service.StoreDetailService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreDetailController {

    private final StoreDetailService storeDetailService;

    @ApiOperation(value = "업장 상세정보를 조회하는 메소드")
    @GetMapping("/detail/{storeId}")
    public StoreResponseDto getStore(
            @PathVariable int storeId,
            @AuthenticationPrincipal User user) {
        return storeDetailService.getStore(storeId, user);
    }
}
