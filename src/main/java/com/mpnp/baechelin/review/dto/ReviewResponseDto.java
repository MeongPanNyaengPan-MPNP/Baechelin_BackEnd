package com.mpnp.baechelin.review.dto;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.tag.domain.Tag;
import com.mpnp.baechelin.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ReviewResponseDto {


    private int reviewId; // storeId
    private long storeId;
    private int userId;

    private String email;

    private String name;

    private String userImage;

    private String myReview;

    private Double point;
    private String content;
    private List<ReviewImageResponseDto> reviewImageUrlList;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<TagResponseDto> tagList;

    public ReviewResponseDto(Review review) {
        this.reviewId   = review.getId();
        this.storeId    = review.getStoreId().getId();
        this.userId     = review.getUserId().getId();
        this.point      = Math.round(review.getPoint()*10)/10.0; // 반올림
        this.content    = review.getContent();
        this.createdAt  = review.getCreatedAt();
        this.modifiedAt = review.getModifiedAt();


        this.reviewImageUrlList = review.getReviewImageList().parallelStream().map(ReviewImageResponseDto::new).collect(Collectors.toList());
        this.tagList            = review.getTagList().parallelStream().map(TagResponseDto::new).collect(Collectors.toList());
    }

    public void userInfo(User user, User myUser){
        this.email    = user.getEmail();
        this.name     = user.getName();
        this.userImage = user.getProfileImageUrl();

        if (myUser != null) {
            if(this.userId == myUser.getId()){
                this.myReview = "Y";
            } else if (this.userId != myUser.getId()){
                this.myReview = "N";
            }
        } else {
            this.myReview = "N";
        }
    }

    public void userInfo(User user){
        this.email    = user.getEmail();
        this.name     = user.getName();
        this.userImage = user.getProfileImageUrl();
        this.myReview = "N";
    }
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class TagResponseDto{
        private int id;
        private String tag;

        public TagResponseDto(Tag tag) {
            this.id = tag.getId();
            this.tag = tag.getTag();
        }
    }
}