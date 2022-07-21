package com.mpnp.baechelin.store.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter @Setter
@Builder
public class StoreSingleResultDto {

    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class LatLong{
        @Builder.Default
        private boolean status = true;
        private String latitude;
        private String longitude;

        public boolean validate() {
            return this.latitude != null && this.longitude != null;
        }
    }
}
