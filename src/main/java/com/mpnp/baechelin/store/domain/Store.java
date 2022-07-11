package com.mpnp.baechelin.store.domain;


import com.mpnp.baechelin.api.dto.PublicApiResponseDto;
import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Store {
    @Id
    private int id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, precision = 25, scale = 22)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 25, scale = 22)
    private BigDecimal longitude;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String elevator;

    @Column(nullable = false)
    private String toilet;

    @Column(nullable = false)
    private String parking;

    //    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String heightDifferent;

    @Column(nullable = false)
    private String approach;

    // 연관관계 매핑
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreImage> storeImageList;

    public Store(PublicApiResponseDto.Row row) {
        //storeId - 임시
        this.id = Integer.parseInt(row.getStoreId());
        this.name = row.getSISULNAME();
        this.address = row.getADDR();
        this.phoneNumber = row.getTEL();
        //접근로
        this.approach = row.getST1();
        //주차장
        this.parking = row.getST2();
        //높이차이제거
        this.heightDifferent = row.getST3();
        //승강기
        this.elevator = row.getST4();
        //화장실
        this.toilet = row.getST5();

        this.latitude = new BigDecimal(row.getLatitude());
        this.longitude = new BigDecimal(row.getLongitude());
        this.category = row.getCategory();
    }

    @OneToMany(mappedBy = "storeId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "storeId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> BookmarkList = new ArrayList<>();


}