package com.mpnp.baechelin.api.dto;

import com.mpnp.baechelin.api.model.LocationAddressSearchForm;
import com.mpnp.baechelin.api.model.LocationKeywordSearchForm;
import lombok.*;

import java.util.Arrays;

@AllArgsConstructor
@Getter @Setter
@Builder
public class LocationPartDto {

    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class LatLong{
        @Builder.Default
        private boolean status = false;
        private String latitude;
        private String longitude;

        public boolean validate() {
            return this.latitude != null && this.longitude != null;
        }
        public static LatLong convertPart(LocationKeywordSearchForm locationKeywordSearchForm){
            LocationPartDto.LatLong locLl = LocationPartDto.LatLong.builder().build();
            if (locationKeywordSearchForm == null) { // 비어 있을 때 status-false 저장
                return locLl;
            }
            LocationKeywordSearchForm.Documents latLngDoc
                    = Arrays.stream(locationKeywordSearchForm.getDocuments()).findAny().orElse(null);
            if (latLngDoc != null) {
                locLl = LocationPartDto.LatLong.builder()
                        .latitude(latLngDoc.getY())
                        .longitude(latLngDoc.getX())
                        .status(true)
                        .build();
            }
            return locLl;
        }
    }

    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class Address{
        @Builder.Default
        private boolean status = false;
        private String address;
        public static Address formToDto(LocationAddressSearchForm resultRe) {
            LocationPartDto.Address addressInfoDto = LocationPartDto.Address.builder().build();
            if (resultRe == null)
                return addressInfoDto;

            LocationAddressSearchForm.TotalAddress address = Arrays.stream(resultRe.getDocuments()).findFirst().orElse(null);
            if (address == null) {
                return addressInfoDto;
            } else {
                return LocationPartDto.Address.builder()
                        .address(address.getAddress().getAddress_name())
                        .status(true)
                        .build();
            }
        }
    }
}
