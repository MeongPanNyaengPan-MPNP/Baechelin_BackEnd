package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.store.domain.UserRegisterStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRegisterStoreRepository extends JpaRepository<UserRegisterStore, Integer> {
    Page<UserRegisterStore> findAll(Pageable pageable);
}
