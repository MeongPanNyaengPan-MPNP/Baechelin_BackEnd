package com.mpnp.baechelin.login.jwt.config;

import com.mpnp.baechelin.login.jwt.AuthTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// jwt를 사용하기 위한 설정. application.yml에서 jwt secret key값을 뽑아내준다.
@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public AuthTokenProvider jwtProvider() {
        return new AuthTokenProvider(secret);
    }
}
