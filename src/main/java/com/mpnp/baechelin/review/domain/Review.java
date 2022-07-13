package com.mpnp.baechelin.review.domain;

import com.mpnp.baechelin.review.dto.ReviewRequestDto;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.tag.domain.Tag;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.util.TimeStamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;


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
    @Column
    private String reviewImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User userId;

    @OneToMany(mappedBy = "reviewId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tagList;

    @Builder
    public Review(ReviewRequestDto reviewRequestDto, Store store, User user, String url) throws IOException {
        this.point = reviewRequestDto.getPoint();
        this.review = reviewRequestDto.getComment();
        this.reviewImageUrl = url;
        this.storeId = store;
        this.userId = user;
    }

}
