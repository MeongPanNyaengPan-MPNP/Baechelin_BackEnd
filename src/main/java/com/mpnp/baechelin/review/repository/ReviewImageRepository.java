package com.mpnp.baechelin.review.repository;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {
    void deleteAllByReviewId(Review review);

    void deleteByReviewId(Review review);
}
