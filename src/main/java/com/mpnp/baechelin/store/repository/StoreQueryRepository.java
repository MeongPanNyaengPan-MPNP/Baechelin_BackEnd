package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static com.mpnp.baechelin.config.QuerydslConfig.locationBuilder;
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

    // TODO 카테고리, 시설 추가하기
    public List<Store> findBetweenLngLat(BigDecimal latStart,
                                         BigDecimal latEnd,
                                         BigDecimal lngStart,
                                         BigDecimal lngEnd,
                                         String category,
                                         List<String> facility,
                                         Pageable pageable) {

        BooleanBuilder builder = locAndConditions(latStart, latEnd, lngStart, lngEnd, category, facility);
        BigDecimal nowLat = (latStart.add(latEnd)).divide(new BigDecimal("2"), 22, RoundingMode.HALF_UP);
        BigDecimal nowLng = (lngStart.add(lngEnd)).divide(new BigDecimal("2"), 22, RoundingMode.HALF_UP);
        List<Store> storeResultList = queryFactory.selectFrom(store)
                .where(builder)
                .fetch();
        // 가까운순으로 정렬하기
        storeResultList.sort((thisStore, newStore) -> {
            BigDecimal thisDiff = nowLat.subtract(thisStore.getLatitude()).abs().add(nowLng.subtract(thisStore.getLongitude()).abs());
            BigDecimal newDiff = nowLat.subtract(newStore.getLatitude()).abs().add(nowLng.subtract(newStore.getLongitude()).abs());
            return thisDiff.compareTo(newDiff);
        });
        // 총 페이지 개수 * 시작 페이지 = 시작 페이지
        getStorePaged(storeResultList, pageable);
        return storeResultList;
//  업데이트 쿼리
//            return queryFactory.selectFrom(store)
//                    .where(builder)
//                    .limit(pageable.getPageSize())
//                    .offset(pageable.getOffset())
//                    .fetch();
    }

    //TODO 별점순 - 쿼리 결과로 산출된 리스트의 평균 구하기, 정렬, 페이징
    public List<Store> findStoreOrderByPoint(BigDecimal lat,
                                             BigDecimal lng,
                                             String category,
                                             List<String> facility,
                                             Pageable pageable) {


        BooleanBuilder builder = locTwoPointAndConditions(lat, lng, category, facility);

        List<Store> resultList = queryFactory.selectFrom(store)
                .where(builder)
                .fetch();

        List<StoreResponseDto> resultAvgList = resultList.stream().sorted()
                .map(StoreResponseDto::new).collect(Collectors.toList());

        getStorePaged(resultAvgList, pageable);


//        return queryFactory.selectFrom(store)
//                .where(builder)
//                .limit(pageable.getPageSize())
//                .offset(pageable.getOffset())
//                .orderBy(store.pointAvg.desc())
//                .fetch();
    }

    //TODO 북마크순
    public List<Store> findStoreOrderByBookmark(BigDecimal lat,
                                                BigDecimal lng,
                                                String category,
                                                List<String> facility,
                                                int limit) {

        BooleanBuilder builder = locTwoPointAndConditions(lat, lng, category, facility);

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
        if (dbFacility.equals("toilet"))
            return store.toilet;
        throw new IllegalArgumentException("배리어 프리 태그를 확인해주세요");
    }

    private BooleanBuilder locAndConditions(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, String category, List<String> facility) {
        BooleanBuilder builder = locationBuilder(latStart, latEnd, lngStart, lngEnd);
        return getBooleanBuilder(category, facility, builder);
    }


    private BooleanBuilder locTwoPointAndConditions(BigDecimal latitude, BigDecimal longitude, String category, List<String> facility) {
        BooleanBuilder builder = new BooleanBuilder();
        if (latitude != null && longitude != null) {
            BigDecimal[] location = getRange(latitude, longitude, 20);
            builder = locationBuilder(location[0], location[1], location[2], location[3]);
        }
        return getBooleanBuilder(category, facility, builder);
    }

    private BooleanBuilder getBooleanBuilder(String category, List<String> facility, BooleanBuilder builder) {
        builder.and(category == null ? null : store.category.eq(category));
        if (facility != null && facility.size() > 0) {
            for (String fac : facility) {
                builder.and(facilityTF(fac));
            }
        }
        return builder;
    }

    private BigDecimal[] getRange(BigDecimal lat, BigDecimal lng, int km) {
        // km->lat,lng로 변환하기
        final BigDecimal latitude = BigDecimal.valueOf(km / 110.569); // 반경
        final BigDecimal longitude = BigDecimal.valueOf(km / 111.322);
        // 남서, 북동으로 받아오기
        // start lat-lng, end lat-lng으로 Array 받아오기
        return new BigDecimal[]{lat.subtract(latitude), lat.add(latitude),
                lng.subtract(longitude), lng.add(longitude)};
    }

    private void getStorePaged(List<?> storeResultList, Pageable pageable) {
        int pageStartIndex = Long.valueOf(storeResultList.size() / pageable.getPageSize() * pageable.getOffset()).intValue();

        // index 처리하기
        int start = 0, end = storeResultList.size();
        start = Math.max(start, pageStartIndex);
        end = Math.min(end, pageStartIndex + pageable.getPageSize() - 1);

        storeResultList =  storeResultList.subList(start, end);
    }

}
