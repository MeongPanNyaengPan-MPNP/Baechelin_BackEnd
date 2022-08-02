package com.mpnp.baechelin.storeApiUpdate.repository;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.storeApiUpdate.StoreApiUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreApiUpdateRepository extends JpaRepository<StoreApiUpdate, Long> {
}