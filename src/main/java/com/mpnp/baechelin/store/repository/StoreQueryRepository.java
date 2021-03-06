package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.common.QuerydslLocation;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mpnp.baechelin.common.QuerydslLocation.locTwoPointAndConditions;
import static com.mpnp.baechelin.store.domain.QStore.store;

@Repository
@Transactional
@Slf4j
public class StoreQueryRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public StoreQueryRepository(JPAQueryFactory queryFactory) {
        super(Store.class);
        this.queryFactory = queryFactory;
    }

    public Page<Store> findBetweenLngLat(BigDecimal latStart,
                                         BigDecimal latEnd,
                                         BigDecimal lngStart,
                                         BigDecimal lngEnd,
                                         String category,
                                         List<String> facility,
                                         Pageable pageable) {
        BigDecimal nowLat = (latStart.add(latEnd)).divide(new BigDecimal("2"), 22, RoundingMode.HALF_UP);
        BigDecimal nowLng = (lngStart.add(lngEnd)).divide(new BigDecimal("2"), 22, RoundingMode.HALF_UP);
        BooleanBuilder builder = QuerydslLocation.locAndConditions(latStart, latEnd, lngStart, lngEnd, category, facility);
        NumberPath<BigDecimal> diff = Expressions.numberPath(BigDecimal.class, "diff");
        List<Tuple> tupleList =
                queryFactory
                        .select(store,
                                store.latitude.subtract(nowLat).abs().add(store.longitude.subtract(nowLng)).abs().as(diff))
                        .from(store)
                        .where(builder)
                        .orderBy(diff.asc())
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset())
                        .fetch();
        List<Store> storeList = tupleList.stream().map(tuple -> tuple.get(store)).collect(Collectors.toList());
        int fetchCount = queryFactory.selectFrom(store).where(builder).fetch().size();
        return new PageImpl<>(storeList, pageable, fetchCount);
    }

    //TODO ????????? - ?????? ????????? ????????? ???????????? ?????? ?????????, ??????, ?????????
    public Page<Store> findStoreOrderByPoint(BigDecimal lat,
                                             BigDecimal lng,
                                             String category,
                                             List<String> facility,
                                             Pageable pageable) {

        BooleanBuilder builder = locTwoPointAndConditions(lat, lng, category, facility);
        NumberPath<BigDecimal> diff = Expressions.numberPath(BigDecimal.class, "diff");
        List<Tuple> tupleList = queryFactory
                .select(store,
                        store.latitude.subtract(lat).abs().add(store.longitude.subtract(lng)).abs().as(diff))
                .from(store)
                .where(builder)
                .orderBy(store.pointAvg.desc())
                .orderBy(diff.asc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        List<Store> storeList = tupleList.stream().map(tuple -> tuple.get(store)).collect(Collectors.toList());
        int fetchCount = queryFactory.selectFrom(store).where(builder).fetch().size();
        return new PageImpl<>(storeList, pageable, fetchCount);
    }

    //TODO ????????????
    public Page<Store> findStoreOrderByBookmark(BigDecimal lat,
                                                BigDecimal lng,
                                                String category,
                                                List<String> facility,
                                                Pageable pageable) {

        BooleanBuilder builder = locTwoPointAndConditions(lat, lng, category, facility);

        List<Store> storeList = queryFactory.selectFrom(store)
                .where(builder)
                .orderBy(store.bookMarkCount.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
        int fetchCount = queryFactory.selectFrom(store).where(builder).fetch().size();
        return new PageImpl<>(storeList, pageable, fetchCount);
    }

}
