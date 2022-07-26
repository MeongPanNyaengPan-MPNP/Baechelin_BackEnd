package com.mpnp.baechelin.api.dto;

import lombok.*;

import java.util.ArrayList;
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
    }
}
