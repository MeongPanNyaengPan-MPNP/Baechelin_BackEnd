package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.domain.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    @Modifying
    @Query("UPDATE Store s set s.pointAvg = :avg where s.id = :storeId")
    void updateAvg(@Param("avg") double avg, @Param("storeId") Long storeId);

    @Query("SELECT AVG(p.point) FROM Store s join s.reviewList p where s.id = :id")
    double getAvg(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Store s set s.bookMarkCount = :count where s.id = :storeId")
    void updateBookmarkCnt(@Param("count") int count, @Param("storeId") Long storeId);

    @Query("SELECT COUNT(b) FROM Store s join s.bookmarkList b where s.id = :id")
    int getBookmarkCnt(@Param("id") Long id);
}