package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.store.domain.UserRegisterStore;
import com.mpnp.baechelin.store.domain.UserRegisterStoreImg;
import com.mpnp.baechelin.store.dto.userRegisterStore.UserRegisterStoreRequestDto;
import com.mpnp.baechelin.store.repository.UserRegisterStoreImgRepository;
import com.mpnp.baechelin.store.repository.UserRegisterStoreRepository;
import com.mpnp.baechelin.user.entity.user.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import com.mpnp.baechelin.util.AwsS3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRegisterStoreService {

    private final AwsS3Manager awsS3Manager;
    private final UserRegisterStoreRepository userRegisterStoreRepository;
    private final UserRegisterStoreImgRepository userRegisterStoreImgRepository;
    private final UserRepository userRepository;


    /**
     * 유저 업장 등록
     * @param userRegisterStoreRequestDto
     */
    public void registerStore(UserRegisterStoreRequestDto userRegisterStoreRequestDto, String socialId) {
        User user = userRepository.findBySocialId(socialId);

        // 업장 등록
        UserRegisterStore userRegisterStore = UserRegisterStore.builder()
                .name(userRegisterStoreRequestDto.getName())
                .address(userRegisterStoreRequestDto.getAddress())
                .elevator(userRegisterStoreRequestDto.getElevator())
                .toilet(userRegisterStoreRequestDto.getToilet())
                .heightDifferent(userRegisterStoreRequestDto.getHeightDifferent())
                .approach(userRegisterStoreRequestDto.getApproach())
                .user(user)
                .build();

        // 업장의 이미지 여러개 등록
        // s3에 이미지 업로드 후 url 반환
        List<String> uploadedImage = awsS3Manager.uploadFile(userRegisterStoreRequestDto.getUserRegisterStoreImageList());

        // saveAll을 위해 userRegisterStoreImg List에 저장
        List<UserRegisterStoreImg> userRegisterStoreImgList = new ArrayList<>();

        for (String image : uploadedImage) {
            UserRegisterStoreImg userRegisterStoreImg = UserRegisterStoreImg.builder()
                    .userRegisterStoreImageUrl(image)
                    .userRegisterStore(userRegisterStore)
                    .build();

            userRegisterStoreImgList.add(userRegisterStoreImg);
        }

        userRegisterStoreRepository.save(userRegisterStore);
        userRegisterStoreImgRepository.saveAll(userRegisterStoreImgList);
    }
}
