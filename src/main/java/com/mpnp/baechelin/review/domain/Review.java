package com.mpnp.baechelin.review.domain;

import com.mpnp.baechelin.review.dto.ReviewReqDTO;
import com.mpnp.baechelin.review.service.ReviewService;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.tag.domain.Tag;
import com.mpnp.baechelin.user.entity.user.User;
import com.mpnp.baechelin.util.TimeStamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.IOException;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //평가(댓글)내용
    @Column(nullable = false)
    private String review;

    //별점
    @Column(nullable = false)
    private double point;

    //리뷰 이미지 URL
    @Column(nullable = true)
    private String reviewImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID", nullable = false)
    private Tag tagId;

    @Builder
    public Review (ReviewReqDTO reviewReqDTO, Store store, Tag tag, User user, String url) throws IOException {

        if(reviewReqDTO.getImageFile() != null) {           //이미지 파일이 있을 경우
            System.out.println("reviewReqDTO.getImageFile() != null");
            ReviewService reviewService = null;
            this.point          = reviewReqDTO.getPoint();
            this.review         = reviewReqDTO.getComment();
            this.reviewImageUrl = url;
            this.storeId        = store;
            this.tagId          = tag;
            this.userId         = user;

        } else if(reviewReqDTO.getImageFile() == null) {    //이미지 파일이 없을 경우
            System.out.println("reviewReqDTO.getImageFile() == null");
            this.point      = reviewReqDTO.getPoint();
            this.review     = reviewReqDTO.getComment();
            this.storeId    = store;
            this.tagId      = tag;
            this.userId     = user;
        }
    }

}
