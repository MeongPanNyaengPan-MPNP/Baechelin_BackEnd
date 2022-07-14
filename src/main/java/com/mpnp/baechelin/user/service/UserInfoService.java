package com.mpnp.baechelin.user.service;

import com.mpnp.baechelin.user.dto.UserInfoResponseDto;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserRepository userRepository;

    public UserInfoResponseDto giveUserInfo(String socialId) {
        User targetUser = userRepository.findBySocialId(socialId);
        return new UserInfoResponseDto(targetUser);
    }
}
