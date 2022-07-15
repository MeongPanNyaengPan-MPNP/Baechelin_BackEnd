package com.mpnp.baechelin.login.oauth.service;

import com.mpnp.baechelin.login.oauth.entity.UserPrincipal;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository.findBySocialId(name);
        if (user == null) {
            throw new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.");
        }
        return UserPrincipal.create(user);
    }
}
