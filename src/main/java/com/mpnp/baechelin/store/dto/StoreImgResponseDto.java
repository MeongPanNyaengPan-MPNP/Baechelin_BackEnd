package com.mpnp.baechelin.store.dto;

import com.mpnp.baechelin.store.domain.StoreImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreImgResponseDto {
    private String storeImageUrl;
    public StoreImgResponseDto(StoreImage storeImage){
        this.storeImageUrl = storeImage.getStoreImageUrl();
    }
    public StoreImgResponseDto(String storeImageUrl) {
        this.storeImageUrl = storeImageUrl;
    }
}
