package com.mpnp.baechelin.common;

import com.mpnp.baechelin.store.domain.Category;

public class DataClarification {
    public static String clarifyString(String input){
        // Trim
        input = input.trim();
        // replace all blanks
        return input.replaceAll("\\s+", " ");
    }
    /**
     * @param category 변환할 카테고리
     * @return 카테고리의 중분류를 추출해 반환
     */
    public static String categoryFilter(String category) {
        if (category == null) {
            return Category.ETC.getDesc();
        } else if (category.contains(">")) {
            return Category.giveCategory(category.split(" > ")[1]).getDesc();
        } else {
            return null;
        }
    }
}
