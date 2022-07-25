package com.mpnp.baechelin.login.jwt.repository;

import com.mpnp.baechelin.login.jwt.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    UserRefreshToken findBySocialId(String socialId);
    UserRefreshToken findBySocialIdAndRefreshToken(String socialId, String refreshToken);
    void deleteByRefreshToken(String refreshToken);
}
