package com.mpnp.baechelin.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class LocationKeywordSearchForm {
    private Meta meta;
    private Documents[] documents;
    @NoArgsConstructor
    @Getter @Setter
    public static class Meta{
        private int total_count;
        private int pageable_count;
        private boolean is_end;
        private SameInfo same_name;
    }
    @NoArgsConstructor
    @Getter @Setter
    public static class SameInfo{
        private String[] region;
        private String keyword;
        private String selected_region;
    }
    @NoArgsConstructor
    @Getter @Setter
    public static class Documents{
        private String id;
        private String place_name;
        private String category_name;
        private String category_group_code;
        private String phone;
        private String address_name;
        private String road_address_name;
        private String x;
        private String y;
        private String place_url;
        private String distance;
    }
}
