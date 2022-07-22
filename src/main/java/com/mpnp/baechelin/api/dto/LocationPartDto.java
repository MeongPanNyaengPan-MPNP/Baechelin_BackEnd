package com.mpnp.baechelin.api.dto;

import lombok.*;

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
    }

    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class Address{
        @Builder.Default
        private boolean status = false;
        private String address;
    }
}
