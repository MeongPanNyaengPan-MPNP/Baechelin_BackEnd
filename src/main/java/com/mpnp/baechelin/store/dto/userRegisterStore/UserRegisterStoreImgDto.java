package com.mpnp.baechelin.store.dto.userRegisterStore;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UserRegisterStoreImgDto {
    private String userRegisterStoreImageUrl;

    public UserRegisterStoreImgDto(String userRegisterStoreImageUrl) {
        this.userRegisterStoreImageUrl = userRegisterStoreImageUrl;
    }
}
