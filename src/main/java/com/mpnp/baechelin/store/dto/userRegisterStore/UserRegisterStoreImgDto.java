package com.mpnp.baechelin.store.dto.userRegisterStore;

import com.mpnp.baechelin.store.domain.UserRegisterStoreImg;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class UserRegisterStoreImgDto {
    private String userRegisterStoreImageUrl;

    public UserRegisterStoreImgDto(String userRegisterStoreImageUrl) {
        this.userRegisterStoreImageUrl = userRegisterStoreImageUrl;
    }

    public UserRegisterStoreImgDto(UserRegisterStoreImg userRegisterStoreImg) {
        this.userRegisterStoreImageUrl = userRegisterStoreImg.getUserRegisterStoreImageUrl();
    }
}
