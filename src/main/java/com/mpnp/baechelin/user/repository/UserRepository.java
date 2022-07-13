package com.mpnp.baechelin.user.repository;

import com.mpnp.baechelin.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findBySocialId(String socialId);

    User findByEmail(String email);
}
