package com.mpnp.baechelin.store.domain;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.util.TimeStamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreImage extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String storeImageUrl;

    // 연관관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID")
    private Review review;

    public void setReview(Review review) {
        this.review = review;
    }


    // 값이 많지 않아 builder 생략
    public StoreImage(String storeImageUrl) {
        this.storeImageUrl = storeImageUrl;
    }
}
