package com.mpnp.baechelin.store.dto;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.store.domain.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDetailResponseDto {
    private long storeId;
    private String category;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String elevator;
    private String toilet;
    private String parking;
    private String phoneNumber;
    private String heightDifferent;
    private String approach;
    private List<String> storeImgList;
    private int bookmarkCount;
    private String bookmark;
    @Builder.Default
    private double pointAvg = 0.0;

    public StoreDetailResponseDto(Store store, String isBookMark, List<String> imageList) {
        this.storeId = store.getId();
        this.category = store.getCategory();
        this.name = store.getName();
        this.latitude = store.getLatitude();
        this.longitude = store.getLongitude();
        this.address = store.getAddress();
        this.elevator = store.getElevator();
        this.toilet = store.getToilet();
        this.parking = store.getParking();
        this.phoneNumber = store.getPhoneNumber();
        this.heightDifferent = store.getHeightDifferent();
        this.approach = store.getApproach();
        this.bookmarkCount = store.getBookMarkCount();
        this.storeImgList = imageList;
        this.pointAvg = Double.parseDouble(String.format(store.getReviewList().stream()
                .collect(Collectors.averagingDouble(Review::getPoint)).toString(), 0.1f));
        this.bookmark = isBookMark;
    }
}
