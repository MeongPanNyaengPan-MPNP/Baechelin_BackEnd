package com.mpnp.baechelin.user.service;

import com.mpnp.baechelin.user.repository.UserRepository;
import com.mpnp.baechelin.util.KakaoAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KakaoAPI kakaoAPI;


    public String getKakaoToken(String code) {
        String accessToken = kakaoAPI.getKakaoAccessToken(code);

        HashMap<String, Object> userInfo = kakaoAPI.getUserInfo(accessToken);
        System.out.println("userInfo : " + userInfo);

        return accessToken;
    }


}
