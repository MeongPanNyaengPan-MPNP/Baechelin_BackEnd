package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.config.QuerydslConfiguration;
import com.mpnp.baechelin.review.repository.ReviewQueryRepository;
import com.mpnp.baechelin.store.domain.QStore;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.tag.domain.QTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.mpnp.baechelin.store.domain.QStore.store;

@Repository
@Transactional
public class StoreQueryRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;
    private final BooleanBuilder builder = new BooleanBuilder();

    public StoreQueryRepository(JPAQueryFactory queryFactory) {
        super(Store.class);
        this.queryFactory = queryFactory;
    }

    // TODO 카테고리, 시설 추가하기
    public List<Store> findBetweenLngLat(BigDecimal latStart,
                                         BigDecimal latEnd,
                                         BigDecimal lngStart,
                                         BigDecimal lngEnd,
                                         String category,
                                         List<String> facility,
                                         Pageable pageable) {

        locAndConditions(latStart, latEnd, lngStart, lngEnd, category, facility);

        return queryFactory.selectFrom(store)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }

    //TODO 별점순
    public List<Store> findStoreOrderByPoint(BigDecimal latStart,
                                             BigDecimal latEnd,
                                             BigDecimal lngStart,
                                             BigDecimal lngEnd,
                                             String category,
                                             List<String> facility,
                                             int limit) {


        locAndConditions(latStart, latEnd, lngStart, lngEnd, category, facility);

        return queryFactory.selectFrom(store)
                .where(builder)
                .orderBy(store.pointAvg.desc())
                .limit(limit)
                .fetch();
    }

    //TODO 북마크순
    public List<Store> findStoreOrderByBookmark(BigDecimal latStart,
                                                BigDecimal latEnd,
                                                BigDecimal lngStart,
                                                BigDecimal lngEnd,
                                                String category,
                                                List<String> facility,
                                                int limit) {

        locAndConditions(latStart, latEnd, lngStart, lngEnd, category, facility);

        return queryFactory.selectFrom(store)
                .where(builder)
                .orderBy(store.bookMarkCount.desc())
                .limit(limit)
                .fetch();

    }

    private BooleanExpression facilityTF(String facility) {
        if (facility == null || facility.isEmpty()) return null;
        return givePath(facility).eq("Y");
    }

    private StringPath givePath(String dbFacility) {
        if (dbFacility.equals("elevator"))
            return store.elevator;
        if (dbFacility.equals("heightDifferent"))
            return store.heightDifferent;
        if (dbFacility.equals("parking"))
            return store.parking;
        if (dbFacility.equals("approach"))
            return store.approach;
        else
            return store.toilet;
    }

    private void locAndConditions(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, String category, List<String> facility) {
        QuerydslConfiguration.locationBuilder(latStart, latEnd, lngStart, lngEnd, builder);
        builder.and(category == null ? null : store.category.eq(category));
        if (facility != null && facility.size() > 0) {
            for (String fac : facility) {
                builder.and(facilityTF(fac));
            }
        }
    }
}
