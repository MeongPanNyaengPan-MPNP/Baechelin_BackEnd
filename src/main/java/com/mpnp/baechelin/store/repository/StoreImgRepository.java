package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.domain.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StoreImgRepository extends JpaRepository<StoreImage, Integer> {

    List<StoreImage> findAllByStoreId(Long storeId);

    StoreImage findByStoreId(long storeId);
}
