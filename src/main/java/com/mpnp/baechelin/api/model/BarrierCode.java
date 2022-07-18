package com.mpnp.baechelin.api.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BarrierCode {
    ELEVATOR("계단 또는 승강설비", "elevator"),
    TOILET_A("소변기", "toilet"),
    TOILET_B("대변기", "toilet"),
    PARKING("장애인전용주차구역", "parking"),
    HEIGHT_DIFFERENCE("주출입구 높이차이 제거", "height_different"),
    APPROACH("주출입구 접근로", "approach"),
    ETC("", null);
    private final String desc;
    private final String columnName;

    BarrierCode(String desc, String columnName) {
        this.desc = desc;
        this.columnName = columnName;
    }

    public static String getColumnFromDesc(String desc) {
        BarrierCode barrierCode = Arrays.stream(BarrierCode.values())
                .filter(b -> b.getDesc().equals(desc)).findFirst().orElse(ETC);
        return barrierCode.getColumnName();
    }

}
