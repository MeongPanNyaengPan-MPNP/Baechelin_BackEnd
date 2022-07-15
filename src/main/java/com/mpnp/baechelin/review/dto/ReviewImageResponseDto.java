package com.mpnp.baechelin.review.dto;

import com.mpnp.baechelin.review.domain.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewImageResponseDto {
    private String url;
    public ReviewImageResponseDto(ReviewImage reviewImage) {
        this.url = reviewImage.getReviewImageUrl();
    }
}
