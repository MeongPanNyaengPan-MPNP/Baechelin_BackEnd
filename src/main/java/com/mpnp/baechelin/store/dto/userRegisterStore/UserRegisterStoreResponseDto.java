package com.mpnp.baechelin.store.dto.userRegisterStore;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class UserRegisterStoreResponseDto {
    private String name;
    private String address;
    private String elevator;
    private String toilet;
    private String heightDifferent;
    private String approach;
    private List<UserRegisterStoreImgDto> userRegisterStoreImageList;
    public UserRegisterStoreResponseDto(
            String name,
            String address,
            String elevator,
            String toilet,
            String heightDifferent,
            String approach,
            List<UserRegisterStoreImgDto> userRegisterStoreImageList
    ) {
        this.name = name;
        this.address = address;
        this.elevator = elevator;
        this.toilet = toilet;
        this.heightDifferent = heightDifferent;
        this.approach = approach;
        this.userRegisterStoreImageList = userRegisterStoreImageList;
    }
}
