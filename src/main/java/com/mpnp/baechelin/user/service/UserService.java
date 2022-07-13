package com.mpnp.baechelin.user.service;

import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(String socialId) {
        return userRepository.findBySocialId(socialId);
    }
}

