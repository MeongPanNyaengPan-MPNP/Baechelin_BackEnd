package com.mpnp.baechelin.admin.repository;

import com.mpnp.baechelin.store.domain.UserRegisterStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<UserRegisterStore, Integer> {
}
