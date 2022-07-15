package com.mpnp.baechelin.store.domain;

import lombok.Getter;

import java.util.Arrays;
@Getter
public enum Category {
    KOREAN("한식"), WESTERN("양식"), JAPANESE("일식"), ASIAN("아시안음식"), CHINESE("중식"),
    FAMILY("패밀리레스토랑"), SNACK("간식"), CAFE("카페"), HOFF("술집"),
    ETC("기타");
    private final String desc;

    public static Category giveCategory(String input){
        return Arrays.stream(Category.values()).filter(cate -> cate.desc.equals(input)).findAny().orElse(Category.ETC);
    }

    Category(String desc) {
        this.desc = desc;
    }
}
