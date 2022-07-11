package com.mpnp.baechelin.user.repository;

import com.mpnp.baechelin.user.entity.user.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    UserRefreshToken findBySocialId(String socialId);
    UserRefreshToken findBySocialIdAndRefreshToken(String socialId, String refreshToken);
}
