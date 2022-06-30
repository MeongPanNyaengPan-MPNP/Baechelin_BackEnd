package com.mpnp.baechelin.review.domain;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.tag.domain.Tag;
import com.mpnp.baechelin.util.TimeStamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Review extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //평가(댓글)내용
    @Column(nullable = false)
    private String review;

    //별점
    @Column(nullable = false)
    private int point;

    //리뷰 이미지 URL
    @Column(nullable = false)
    private String reviewImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Store userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID", nullable = false)
    private Tag tagId;

//    private int userId;
//    private int storeId;

}
