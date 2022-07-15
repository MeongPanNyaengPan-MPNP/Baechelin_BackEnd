package com.mpnp.baechelin.review.dto;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewMainResponseDto {
    // review 테이블 컬럼
    private int storeId;
    private int userId;
    private String storeName;
    private String userName;
    private String content; //리뷰 코멘트
    private double point; //별점
    private List<ReviewImageResponseDto> reviewImageUrlList; //리뷰 이미지 사진
    private List<ReviewResponseDto.TagResponseDto> tagList;
    public ReviewMainResponseDto(Review review, Store store, User user) {
        this.storeId = store.getId();
        this.userId = user.getId();
        this.storeName = store.getName();
        this.userName = user.getName();
        this.content = review.getContent();
        this.point = review.getPoint();
        this.reviewImageUrlList = review.getReviewImageList()
                .parallelStream().map(ReviewImageResponseDto::new).collect(Collectors.toList());
        this.tagList = review.getTagList().parallelStream().map(ReviewResponseDto.TagResponseDto::new).collect(Collectors.toList());
    }
}