package com.mpnp.baechelin.review.repository;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {
    void deleteAllByReviewId(Review review);
    void deleteByReviewId(Review review);
}
