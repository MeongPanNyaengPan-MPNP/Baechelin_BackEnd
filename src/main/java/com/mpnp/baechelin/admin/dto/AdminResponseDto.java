package com.mpnp.baechelin.admin.dto;

import com.mpnp.baechelin.store.dto.userRegisterStore.UserRegisterStoreImgDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdminResponseDto {
    private String name;
    private String address;
    private String elevator;
    private String toilet;
    private String heightDifferent;
    private String approach;
    private List<UserRegisterStoreImgDto> userRegisterStoreImageList;

    @Builder
    public AdminResponseDto(
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
