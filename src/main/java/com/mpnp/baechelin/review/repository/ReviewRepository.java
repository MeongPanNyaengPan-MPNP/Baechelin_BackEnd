package com.mpnp.baechelin.review.repository;

import com.mpnp.baechelin.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
}
