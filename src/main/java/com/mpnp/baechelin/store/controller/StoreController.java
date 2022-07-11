package com.mpnp.baechelin.store.controller;

import com.mpnp.baechelin.store.dto.StoreResponseDto;
import com.mpnp.baechelin.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public List<StoreResponseDto> getStoreList() {
        return null;
    }
}
