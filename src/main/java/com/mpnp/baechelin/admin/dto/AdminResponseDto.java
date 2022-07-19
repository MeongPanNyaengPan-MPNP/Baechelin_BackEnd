package com.mpnp.baechelin.admin.dto;

import com.mpnp.baechelin.store.domain.UserRegisterStore;
import com.mpnp.baechelin.store.domain.UserRegisterStoreImg;
import com.mpnp.baechelin.store.dto.userRegisterStore.UserRegisterStoreImgDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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
            UserRegisterStore userRegisterStore
    ) {
        this.name = userRegisterStore.getName();
        this.address = userRegisterStore.getAddress();
        this.elevator = userRegisterStore.getElevator();
        this.toilet = userRegisterStore.getToilet();
        this.heightDifferent = userRegisterStore.getHeightDifferent();
        this.approach = userRegisterStore.getApproach();
        this.userRegisterStoreImageList = userRegisterStore.getUserRegisterStoreImgList().parallelStream()
                .map(UserRegisterStoreImgDto::new).collect(Collectors.toList());
    }
}
