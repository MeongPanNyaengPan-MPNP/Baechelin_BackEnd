package com.mpnp.baechelin.api.dto;

import com.mpnp.baechelin.api.model.LocationKeywordSearchForm;
import com.mpnp.baechelin.common.DataClarification;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LocationInfoDto {
    @Builder.Default
    private boolean isEnd = true;
    @Builder.Default
    private List<LocationResponse> locationResponseMapList = new ArrayList<>();

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class LocationResponse {

        String category;
        Long storeId;
        String storeName;
        String longitude;
        String latitude;
        String phoneNumber;

        public boolean validate() {
            return this.category != null && this.storeId != null && this.latitude != null
                    && this.longitude != null && this.storeName != null;
        }

        public static LocationResponse KeywordToRes(LocationKeywordSearchForm locationKeywordSearchForm){
            if (locationKeywordSearchForm == null) {
                return null;
            }
            LocationKeywordSearchForm.Documents latLngDoc
                    = Arrays.stream(locationKeywordSearchForm.getDocuments()).findFirst().orElse(null);
            if (latLngDoc == null) {
                return null;
            }
            return LocationInfoDto.LocationResponse.builder()
                    .storeId(Long.valueOf(latLngDoc.getId()))
                    .latitude(latLngDoc.getY())
                    .longitude(latLngDoc.getX())
                    .category(DataClarification.categoryFilter(latLngDoc.getCategory_name()))
                    .storeName(latLngDoc.getPlace_name())
                    .phoneNumber(latLngDoc.getPhone()).build();
        }
    }
}
