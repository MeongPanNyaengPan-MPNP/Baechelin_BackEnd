package com.mpnp.baechelin.store.dto;

import com.mpnp.baechelin.review.dto.ReviewResponseDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class StoreResponseDto {
    private int storeId;
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
    private List<StoreImgResponseDto> storeImgList;
    private double pointAvg;
    private String bookmark;
    private List<ReviewResponseDTO> reviewList;

    @Builder
    public StoreResponseDto(
            int storeId,
            String category,
            String name,
            BigDecimal latitude,
            BigDecimal longitude,
            String address,
            String elevator,
            String toilet,
            String parking,
            String phoneNumber,
            String heightDifferent,
            String approach,
            List<StoreImgResponseDto> storeImgList,
            double pointAvg,
            String bookmark,
            List<ReviewResponseDTO> reviewList) {

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
        this.pointAvg = pointAvg;
        this.bookmark = bookmark;
        this.reviewList = reviewList;
    }
}
