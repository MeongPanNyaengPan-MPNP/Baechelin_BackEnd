package com.mpnp.baechelin.api.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDto {
    private TouristFoodInfo touristFoodInfo;

    public static class TouristFoodInfo {
        private int list_total_count;
        private Result RESULT;
        private List<Row> rows;
    }

    public static class Result {
        private String CODE;
        private String MESSAGE;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Row {
        private int SEQ;
        private String SISULNAME;
        private String GU;
        private String ADDR;
        private String TEL;
        private String HOMEPAGE;
        private String HIT;
        private String ST1;
        private String ST2;
        private String ST3;
        private String ST4;
        private String ST5;
        private String ST6;
        private String ST7;
        private String ST8;
        private String ST9;
        private String ST10;
        private String ST11;
        private String ST12;

    }
}
