package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.store.domain.UserRegisterStore;
import com.mpnp.baechelin.store.domain.UserRegisterStoreImg;
import com.mpnp.baechelin.store.dto.userRegisterStore.UserRegisterStoreRequestDto;
import com.mpnp.baechelin.store.repository.UserRegisterStoreImgRepository;
import com.mpnp.baechelin.store.repository.UserRegisterStoreRepository;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import com.mpnp.baechelin.util.AwsS3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
     * @param socialId
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
        // saveAll을 위해 userRegisterStoreImg List에 저장
        List<UserRegisterStoreImg> userRegisterStoreImgList = new ArrayList<>();

        // 유저가 등록한 업장 이미지 리스트
        List<MultipartFile> userRegisterStoreImageFiles = userRegisterStoreRequestDto.getUserRegisterStoreImageList();

        for (MultipartFile userRegisterStoreImageFile : userRegisterStoreImageFiles) {
            UserRegisterStoreImg userRegisterStoreImg = UserRegisterStoreImg.builder()
                    .userRegisterStoreImageUrl(awsS3Manager.uploadFile(userRegisterStoreImageFile)) // 유저가 등록한 업장 이미지 url 변환
                    .userRegisterStore(userRegisterStore)
                    .build();

            userRegisterStoreImgList.add(userRegisterStoreImg);
        }


        userRegisterStoreRepository.save(userRegisterStore);
        userRegisterStoreImgRepository.saveAll(userRegisterStoreImgList);
    }
}
