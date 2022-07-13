package com.mpnp.baechelin.store.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreImgResponseDto {
    private String storeImageUrl;

    public StoreImgResponseDto(String storeImageUrl) {
        this.storeImageUrl = storeImageUrl;
    }
}
