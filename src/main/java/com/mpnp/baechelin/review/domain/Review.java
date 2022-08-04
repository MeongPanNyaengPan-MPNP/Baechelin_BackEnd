package com.mpnp.baechelin.review.domain;

import com.mpnp.baechelin.review.dto.ReviewRequestDto;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.tag.domain.Tag;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.util.TimeStamped;
import lombok.*;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //평가(댓글)내용
    @Column(nullable = false)
    private String content;

    //별점
    @Column(nullable = false)
    private double point;

    //리뷰 이미지 URL
    @OneToMany(mappedBy = "reviewId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImageList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User userId;

    @OneToMany(mappedBy = "reviewId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tagList = new ArrayList<>();


    public Review(ReviewRequestDto reviewRequestDto, Store store, User user) throws IOException {
        this.point   = reviewRequestDto.getPoint();
        this.userId  = user;
        this.content = reviewRequestDto.getContent();
        this.storeId = store;
    }


    public void update(ReviewRequestDto reviewRequestDto){
        this.point   = reviewRequestDto.getPoint();
        this.content = reviewRequestDto.getContent();
    }

    public void addImage(List<ReviewImage> reviewImageList) {
        this.reviewImageList = reviewImageList;
    }
    public void addTag(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public void removeReviewImage(List<ReviewImage> reviewImagelist) {
        for(ReviewImage reviewImage: reviewImagelist){
            this.reviewImageList.remove(reviewImage);
        }
    }
}
