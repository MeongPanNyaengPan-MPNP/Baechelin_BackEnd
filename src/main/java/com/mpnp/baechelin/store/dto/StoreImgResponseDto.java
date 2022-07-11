package com.mpnp.baechelin.store.dto;

import lombok.Getter;

@Getter
public class StoreImgResponseDto {
    private String storeImageUrl;

    public StoreImgResponseDto(String storeImageUrl) {
        this.storeImageUrl = storeImageUrl;
    }
}
