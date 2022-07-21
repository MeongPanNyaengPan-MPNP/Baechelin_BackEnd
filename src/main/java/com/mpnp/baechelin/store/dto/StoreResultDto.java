package com.mpnp.baechelin.store.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class StoreResultDto {
    @Builder.Default
    private boolean isEnd = true;
    @Builder.Default
    private List<StoreResult> storeResultMapList = new ArrayList<>();

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class StoreResult{
        String category;
        Integer storeId;
        String storeName;
        String phoneNumber;

        public boolean validate(){
            return this.category!=null && this.storeId!=null
                    && this.storeName!=null;
        }
    }
}
