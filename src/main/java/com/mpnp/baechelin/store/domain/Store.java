package com.mpnp.baechelin.store.domain;

import com.mpnp.baechelin.api.dto.LocationInfoDto;
import com.mpnp.baechelin.api.model.PublicApiV1Form;
import com.mpnp.baechelin.api.model.PublicApiV2Form;
import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.common.DataClarification;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.storeApiUpdate.StoreApiUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name="Store")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Slf4j
public class Store implements Serializable {
    @Id
    private long id;

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

    @Column(nullable = false)
    private int bookMarkCount = 0;

    @Column(nullable = false)
    private int reviewCount = 0;

    @Column(nullable = false)
    private double pointAvg = 0.0;

    // 연관관계 매핑
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreImage> storeImageList = new ArrayList<>();

    @OneToMany(mappedBy = "storeId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "storeId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();

    public Store(PublicApiV1Form.Row row) {
        //storeId - 임시
        this.id = row.getStoreId();
        this.name = row.getSISULNAME();
        this.address = DataClarification.clarifyString(row.getADDR());
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

        this.latitude = row.getLatitude();
        this.longitude = row.getLongitude();
        this.category = row.getCategory();
    }

    public Store updateBookmarkCount() {
        this.bookMarkCount = this.getBookmarkList().size();
        return this;
    }

    public void removeReview(Review review) {
        this.reviewList.remove(review);
    }

    public void removeBookmark(Bookmark bookmark) {
        this.bookmarkList.remove(bookmark);
    }

    public Store updatePointAvg() {
        this.reviewCount = reviewList.size();
        double totalPoint = 0.0;
        for (Review review : reviewList) {
            totalPoint += review.getPoint();
        }
        this.pointAvg = reviewCount == 0 ? 0 : Double.parseDouble(String.format("%.1f", totalPoint / reviewList.size()));
        return this;
    }

    public Store(LocationInfoDto.LocationResponse sr, PublicApiV2Form.ServList servList, List<String> barrierTagList) {
        this.id = sr.getStoreId();
        this.name = sr.getStoreName();
        this.latitude = new BigDecimal(servList.getFaclLat());
        this.longitude = new BigDecimal(servList.getFaclLng());
        this.address = DataClarification.clarifyString(servList.getLcMnad());
        this.elevator = barrierTagList.contains("elevator") ? "Y" : "N";
        this.heightDifferent = barrierTagList.contains("height_different") ? "Y" : "N";
        this.toilet = barrierTagList.contains("toilet") ? "Y" : "N";
        this.parking = barrierTagList.contains("parking") ? "Y" : "N";
        this.approach = barrierTagList.contains("approach") ? "Y" : "N";

        this.phoneNumber = sr.getPhoneNumber();
        this.category = sr.getCategory();
    }

    public Store(StoreApiUpdate row){
        this.id          = row.getId();
        this.name        = row.getName();
        this.address     = row.getAddress();
        this.phoneNumber = row.getPhoneNumber();

        /* 태그 */
        //접근로
        this.approach        = row.getApproach();
        //주차장
        this.parking         = row.getParking();
        //승강기
        this.elevator        = row.getElevator();
        //화장실
        this.toilet          = row.getToilet();
        //높이차이제거
        this.heightDifferent = row.getHeightDifferent();

        this.category  = row.getCategory();
        this.latitude  = row.getLatitude();
        this.longitude = row.getLongitude();
    }


    public void apiUpdate(StoreApiUpdate row) {
        this.id          = row.getId();
        this.name        = row.getName();
        this.address     = row.getAddress();
        this.phoneNumber = row.getPhoneNumber();

        /* 태그 */
        //접근로
        this.approach        = row.getApproach();
        //주차장
        this.parking         = row.getParking();
        //승강기
        this.elevator        = row.getElevator();
        //화장실
        this.toilet          = row.getToilet();
        //높이차이제거
        this.heightDifferent = row.getHeightDifferent();

        this.category  = row.getCategory();
        this.latitude  = row.getLatitude();
        this.longitude = row.getLongitude();
    }

}