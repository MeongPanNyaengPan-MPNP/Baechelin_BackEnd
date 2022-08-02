package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.common.QueryDslSearch;
import com.mpnp.baechelin.common.QuerydslLocation;
import com.mpnp.baechelin.store.domain.QStore;
import com.mpnp.baechelin.store.domain.Store;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mpnp.baechelin.common.QueryDslSearch.getSearchBooleanBuilder;
import static com.mpnp.baechelin.common.QuerydslLocation.locTwoPointAndConditions;
import static com.mpnp.baechelin.store.domain.QStore.store;
import static com.querydsl.core.types.dsl.Expressions.constant;
import static com.querydsl.core.types.dsl.MathExpressions.*;

@Repository
@Transactional
@Slf4j
public class StoreQueryRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public StoreQueryRepository(JPAQueryFactory queryFactory) {
        super(Store.class);
        this.queryFactory = queryFactory;
    }

    public Page<Store> findBetweenTwoPointOrder(BigDecimal latStart,
                                                BigDecimal latEnd,
                                                BigDecimal lngStart,
                                                BigDecimal lngEnd,
                                                String category,
                                                List<String> facility,
                                                Pageable pageable) {
        BooleanBuilder builder = QuerydslLocation.locAndConditions(latStart, latEnd, lngStart, lngEnd, category, facility);
        if (latStart == null || lngStart == null || lngEnd == null || latEnd == null)
            return findBetweenOnePointOrderNullCase(builder, pageable);
        BigDecimal nowLat = (latStart.add(latEnd)).divide(new BigDecimal("2"), 22, RoundingMode.HALF_UP);
        BigDecimal nowLng = (lngStart.add(lngEnd)).divide(new BigDecimal("2"), 22, RoundingMode.HALF_UP);
        NumberPath<Double> path = Expressions.numberPath(Double.class, "realdist");
        return getNearStores(nowLat, nowLng, pageable, builder, path);
    }


    public Page<Store> findBetweenOnePointOrder(BigDecimal latStart,
                                                BigDecimal latEnd,
                                                BigDecimal lngStart,
                                                BigDecimal lngEnd,
                                                BigDecimal lat,
                                                BigDecimal lng,
                                                String category,
                                                List<String> facility,
                                                Pageable pageable) {
        BooleanBuilder builder = QuerydslLocation.locAndConditions(latStart, latEnd, lngStart, lngEnd, category, facility);
        if (latStart == null || lngStart == null || lngEnd == null || latEnd == null)
            return findBetweenOnePointOrderNullCase(builder, pageable);
        NumberPath<Double> path = Expressions.numberPath(Double.class, "realdist");
        return getNearStores(lat, lng, pageable, builder, path);
    }

    private Page<Store> getNearStores(BigDecimal lat, BigDecimal lng, Pageable pageable, BooleanBuilder builder, NumberPath<Double> path) {
        List<Tuple> tupleList =
                queryFactory
                        .select(store,
                                acos(sin(radians(Expressions.constant(lat)))
                                        .multiply(sin(radians(store.latitude)))
                                        .add(cos(radians(Expressions.constant(lat))).multiply(cos(radians(store.latitude)))
                                                .multiply(cos(radians(Expressions.constant(lng)).subtract(radians(store.longitude)))))).multiply(6371).as(path)
                        )
                        .from(store)
                        .where(builder)
                        .orderBy(path.asc())
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset())
                        .fetch();
        List<Store> storeList = tupleList.stream().map(tuple -> tuple.get(store)).collect(Collectors.toList());
        int fetchCount = queryFactory.selectFrom(store).where(builder).fetch().size();
        return new PageImpl<>(storeList, pageable, fetchCount);
    }

    private Page<Store> findBetweenOnePointOrderNullCase(BooleanBuilder builder,
                                                         Pageable pageable) {
        List<Store> storeList =
                queryFactory
                        .selectFrom(store)
                        .where(builder)
                        .limit(pageable.getPageSize())
                        .fetch();
        return new PageImpl<>(storeList, pageable, storeList.size());
    }

    public Page<Store> findStoreOrderByPoint(BigDecimal lat,
                                             BigDecimal lng,
                                             String category,
                                             List<String> facility,
                                             Pageable pageable) {

        BooleanBuilder builder = locTwoPointAndConditions(lat, lng, category, facility);
        if (lat == null || lng == null) return findStoreOrderByPointNullCase(builder, pageable);
        List<Store> storeList = queryFactory
                .selectFrom(store)
                .where(builder)
                .orderBy(store.pointAvg.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
        int fetchCount = queryFactory.selectFrom(store).where(builder).fetch().size();
        return new PageImpl<>(storeList, pageable, fetchCount);
    }

    private Page<Store> findStoreOrderByPointNullCase(BooleanBuilder builder,
                                                      Pageable pageable) {
        List<Store> storeList = queryFactory
                .selectFrom(store)
                .where(builder)
                .orderBy(store.pointAvg.desc())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(storeList, pageable, storeList.size());
    }

    public Page<Store> findStoreOrderByBookmark(BigDecimal lat,
                                                BigDecimal lng,
                                                String category,
                                                List<String> facility,
                                                Pageable pageable) {
        BooleanBuilder builder = locTwoPointAndConditions(lat, lng, category, facility);
        if (lat == null || lng == null) return findStoreOrderByBookmarkNullCase(builder, pageable);
        List<Store> storeList = queryFactory.selectFrom(store)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
        int fetchCount = queryFactory.selectFrom(store).where(builder).fetch().size();
        return new PageImpl<>(storeList, pageable, fetchCount);
    }

    public Page<Store> findStoreOrderByBookmarkNullCase(BooleanBuilder builder,
                                                        Pageable pageable) {

        List<Store> storeList = queryFactory.selectFrom(store)
                .where(builder)
                .orderBy(store.bookMarkCount.desc())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(storeList, pageable, storeList.size());
    }

    // 시/도 정보로 시/군/구 정보를 조회
    public List<Store> getSigungu(String sido) {
        BooleanExpression matchAddress = QueryDslSearch.matchAddressWithSido(sido);

        return queryFactory
                .selectFrom(store)
                .where(matchAddress)
                .fetch();
    }


    // 주소로 검색, 검색어로 검색
    public Page<Store> searchStores(String sido, String sigungu, String keyword, String category, List<String> facility, Pageable pageable) {
        BooleanBuilder builder = getSearchBooleanBuilder(sido, sigungu, keyword, category, facility);

        List<Store> storeList = queryFactory
                .selectFrom(store)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
        int fetchSize = queryFactory.selectFrom(store)
                .where(builder)
                .fetch().size();
        return new PageImpl<>(storeList, pageable, fetchSize);
    }
}
