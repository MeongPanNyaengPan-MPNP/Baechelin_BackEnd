package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.domain.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
}