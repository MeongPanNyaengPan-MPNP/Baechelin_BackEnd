package com.mpnp.baechelin.tag.domain;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.util.TimeStamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String tag;
    //jpa를 리스트/JSON 집어넣는 방법?

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "REVIEW_ID", nullable = false)
    private Review reviewId;

    public Tag(String tag, Review review) {
        this.tag = tag;
        this.reviewId = review;
    }
    public void setReview(Review review){
        this.reviewId = review;
    }
}
