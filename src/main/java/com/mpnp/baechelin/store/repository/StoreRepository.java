package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Integer> {
}