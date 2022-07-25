package com.mpnp.baechelin.common;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.lang3.StringUtils;

import static com.mpnp.baechelin.store.domain.QStore.store;

public class QueryDslSearch {
    public static BooleanExpression matchAddress(String sido, String sigungu) {
        // sido가 null이면 sigungu는 무조건 null
        // sido가 null이 아니면 sigungu는 null 또는 not null
        if (StringUtils.isEmpty(sido)) {
            return null;
        } else if (StringUtils.isEmpty(sigungu)) {
            return null;
        }
        return Expressions.numberTemplate(
                Integer.class,
                "function('match', {0}, {1}, {2})", store.address, store.address, sido + " " + sigungu).gt(0);
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
