package com.mpnp.baechelin.review.dto;

import com.mpnp.baechelin.review.domain.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {
    int id; // reviewId
    Double point;
    String comment;
    String reviewImageUrl;
    LocalDateTime modifiedAt;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.point = review.getPoint();
        this.comment = review.getReview();
        this.reviewImageUrl = review.getReviewImageUrl();
        this.modifiedAt = review.getModifiedAt();
    }
}