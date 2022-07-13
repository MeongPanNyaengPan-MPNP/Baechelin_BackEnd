package com.mpnp.baechelin.review.dto;

import com.mpnp.baechelin.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewMainResponseDto {
    // review 테이블 컬럼
    private String comment; //리뷰 코멘트
    private double point; //별점
    private String imageFileUrl; //리뷰 이미지 사진

    public ReviewMainResponseDto(Review review) {
        this.comment = review.getReview();
        this.point = review.getPoint();
        this.imageFileUrl = review.getReviewImageUrl();
    }
}