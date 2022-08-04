package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.domain.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreImgRepository extends JpaRepository<StoreImage, Integer> {
    @Query("select si.storeImageUrl from StoreImage si where si.store=:storeId")
    String findLatestImage(@Param("storeId") int storeId);
    List<StoreImage> findAllByStoreId(Long storeId);

    StoreImage findByStoreId(long storeId);
}
