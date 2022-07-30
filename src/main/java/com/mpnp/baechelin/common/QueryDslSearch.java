package com.mpnp.baechelin.common;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.lang3.StringUtils;

import static com.mpnp.baechelin.store.domain.QStore.store;

public class QueryDslSearch {

    public static BooleanExpression matchAddressWithSido(String sido) {
        if (StringUtils.isEmpty(sido)) {
            return null;
        }

        return Expressions.numberTemplate(
                Integer.class,
                "function('match', {0}, {1}, {2})", store.address, store.address, sido).gt(0);
    }
    public static BooleanExpression matchAddressWithSidoAndSigungu(String sido, String sigungu) {

        // sido가 null이면 sigungu는 무조건 null
        if (StringUtils.isEmpty(sido)) {
            return null;
        } else if (StringUtils.isEmpty(sigungu)) {
            // sido가 null이 아니고 sigungu가 null이면 sido 검색 결과 리턴
            return Expressions.numberTemplate(
                    Integer.class,
                    "function('match', {0}, {1}, {2})", store.address, store.address, sido).gt(0);
        } else if (sigungu.split(" ").length > 1) {
            // sigungu가 도/시/구 로 나눠져있을 때 (ex. 경기도 성남시 분당구)
            // 정확한 검색을 위해 + 연산자 추가
            return Expressions.numberTemplate(
                    Integer.class,
                    "function('match', {0}, {1}, {2})", store.address, store.address, sido + " +" + sigungu.split(" ")[0] + " +" + sigungu.split(" ")[1]).gt(0);
        } else {
            return Expressions.numberTemplate(
                    Integer.class,
                    "function('match', {0}, {1}, {2})", store.address, store.address, sido + " +" + sigungu).gt(0);
        }
    }

    public static BooleanExpression matchKeyword(String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            return null;
        }
        return Expressions.numberTemplate(
                Integer.class,
                "function('match', {0}, {1}, {2})", store.name, store.category, keyword).gt(0);
    }
}
