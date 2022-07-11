package com.mpnp.baechelin.store.dto.userRegisterStore;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UserRegisterStoreRequestDto {
    private String name;
    private String address;
    private String elevator;
    private String toilet;
    private String heightDifferent;
    private String approach;
    private List<MultipartFile> userRegisterStoreImageList;
}
