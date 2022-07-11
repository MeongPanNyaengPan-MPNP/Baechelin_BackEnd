package com.mpnp.baechelin.user.service;

import com.mpnp.baechelin.user.dto.UserInfoResponseDto;
import com.mpnp.baechelin.user.entity.user.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserRepository userRepository;

    // TODO Security 추가 후 변경할 예정
    //    public UserInfoResponseDto giveUserInfo(User user){
    public UserInfoResponseDto giveUserInfo(Integer userId) {
        User targetUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("test - no user"));
        return new UserInfoResponseDto(targetUser);
//        return new UserInfoResponseDto(user);
    }
}
