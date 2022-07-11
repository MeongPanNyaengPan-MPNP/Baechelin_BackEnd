package com.mpnp.baechelin.store.domain.repository;

import com.mpnp.baechelin.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {
}
