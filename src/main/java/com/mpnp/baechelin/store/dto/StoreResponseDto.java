package com.mpnp.baechelin.store.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class StoreResponseDto {
    private int storeId;
    private String category;
    private String name;
    private String latitude;
    private String longitude;
    private String address;
    private String elevator;
    private String toilet;
    private String parking;
    private String phoneNumber;
    private String heightDifferent;
    private String approach;
    private List<StoreImgResponseDto> storeImgList;
    private LocalDateTime storeModifiedAt;
    private double pointAvg;
//    private List<ReviewResDTO> reviewList;

    @Builder
    public StoreResponseDto(
            int storeId,
            String category,
            String name,
            String latitude,
            String longitude,
            String address,
            String elevator,
            String toilet,
            String parking,
            String phoneNumber,
            String heightDifferent,
            String approach,
            List<StoreImgResponseDto> storeImgList,
            LocalDateTime storeModifiedAt,
            double pointAvg) {

        this.storeId = storeId;
        this.category = category;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.elevator = elevator;
        this.toilet = toilet;
        this.parking = parking;
        this.phoneNumber = phoneNumber;
        this.heightDifferent = heightDifferent;
        this.approach = approach;
        this.storeImgList = storeImgList;
        this.storeModifiedAt = storeModifiedAt;
        this.pointAvg = pointAvg;
    }
}
