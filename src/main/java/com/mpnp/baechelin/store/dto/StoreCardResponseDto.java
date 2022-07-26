package com.mpnp.baechelin.store.dto;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.user.domain.User;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter @Setter
@Builder
@Slf4j
public class StoreCardResponseDto implements Comparable<StoreCardResponseDto> {
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
    private List<StoreImgResponseDto> storeImgList = new ArrayList<>();
    private int bookmarkCount;
    private String bookmark;

    @Builder.Default
    private double pointAvg = 0.0;

    public StoreCardResponseDto(Store store) {
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
    }

    @Override
    public int compareTo(StoreCardResponseDto sad) {
        if (this.pointAvg > sad.pointAvg) {
            return -1;
        } else if (this.pointAvg < sad.pointAvg) {
            return 1;
        }
        return 0;
    }


    public StoreCardResponseDto(Store store, String isBookMark) {
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
        this.storeImgList = store.getStoreImageList().parallelStream()
                .map(StoreImgResponseDto::new).collect(Collectors.toList());
        this.pointAvg = Double.parseDouble(String.format(store.getReviewList().stream()
                .collect(Collectors.averagingDouble(Review::getPoint)).toString(), 0.1f));
        this.bookmark = isBookMark;
    }
}
