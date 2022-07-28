package com.mpnp.baechelin.review.repository;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findAllByStoreId(Store store);
    Page<Review> findAllByStoreId(Store store, Pageable pageable);

}
