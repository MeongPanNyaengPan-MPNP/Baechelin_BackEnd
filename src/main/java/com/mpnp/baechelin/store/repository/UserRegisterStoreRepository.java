package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.store.domain.UserRegisterStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRegisterStoreRepository extends JpaRepository<UserRegisterStore, Integer> {
}
