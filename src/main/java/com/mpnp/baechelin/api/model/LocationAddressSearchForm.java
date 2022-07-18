package com.mpnp.baechelin.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class LocationAddressSearchForm {
    private LocalMeta meta;
    private TotalAddress[] documents;
    @NoArgsConstructor
    @Getter @Setter
    public static class LocalMeta{
        private Integer total_count;
    }
    @NoArgsConstructor
    @Getter @Setter
    public static class TotalAddress {
        private RoadAddress road_address;
        private Address address;
    }
    @NoArgsConstructor
    @Getter @Setter
    public static class RoadAddress{
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String road_name;
        private String underground_yn;
        private String main_building_no;
        private String sub_building_no;
        private String building_name;
        private String zone_no;
    }
    @NoArgsConstructor
    @Getter @Setter
    public static class Address{
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String mountain_yn;
        private String main_address_no;
        private String sub_address_no;
        private String zip_code;
    }
}
