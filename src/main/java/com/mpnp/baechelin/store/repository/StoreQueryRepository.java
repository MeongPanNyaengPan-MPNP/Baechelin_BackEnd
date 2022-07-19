package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.config.QuerydslLocation;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.querydsl.core.BooleanBuilder;
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
import java.util.List;

import static com.mpnp.baechelin.config.QuerydslLocation.locTwoPointAndConditions;
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
//    public List<Store> findBetweenLngLat(BigDecimal latStart,
    public Page<Store> findBetweenLngLat(BigDecimal latStart,
                                         BigDecimal latEnd,
                                         BigDecimal lngStart,
                                         BigDecimal lngEnd,
                                         String category,
                                         List<String> facility,
                                         Pageable pageable) {

        BooleanBuilder builder = QuerydslLocation.locAndConditions(latStart, latEnd, lngStart, lngEnd, category, facility);

//        BigDecimal nowLat = (latStart.add(latEnd)).divide(new BigDecimal("2"), 22, RoundingMode.HALF_UP);
//        BigDecimal nowLng = (lngStart.add(lngEnd)).divide(new BigDecimal("2"), 22, RoundingMode.HALF_UP);
//        List<Store> storeResultList = queryFactory.selectFrom(store)
//                .where(builder)
//                .fetch();
//        // 가까운순으로 정렬하기
//        storeResultList.sort((thisStore, newStore) -> {
//            BigDecimal thisDiff = nowLat.subtract(thisStore.getLatitude()).abs().add(nowLng.subtract(thisStore.getLongitude()).abs());
//            BigDecimal newDiff = nowLat.subtract(newStore.getLatitude()).abs().add(nowLng.subtract(newStore.getLongitude()).abs());
//            return thisDiff.compareTo(newDiff);
//        });
//        getStorePaged(storeResultList, pageable);

        List<Store> storeList = queryFactory.selectFrom(store)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
        // 가까운순으로 정렬하기
        if (latEnd != null && latStart != null && lngStart != null && lngEnd != null) {
            BigDecimal nowLat = (latStart.add(latEnd)).divide(new BigDecimal("2"), 22, RoundingMode.HALF_UP);
            BigDecimal nowLng = (lngStart.add(lngEnd)).divide(new BigDecimal("2"), 22, RoundingMode.HALF_UP);
            storeList.sort((thisStore, newStore) -> {
                BigDecimal thisDiff = nowLat.subtract(thisStore.getLatitude()).abs().add(nowLng.subtract(thisStore.getLongitude()).abs());
                BigDecimal newDiff = nowLat.subtract(newStore.getLatitude()).abs().add(nowLng.subtract(newStore.getLongitude()).abs());
                return thisDiff.compareTo(newDiff);
            });
        }
        int fetchCount = queryFactory.selectFrom(store).where(builder).fetch().size();
        return new PageImpl<>(storeList, pageable, fetchCount);
    }

    //TODO 별점순 - 쿼리 결과로 산출된 리스트의 평균 구하기, 정렬, 페이징
//    public List<StoreCardResponseDto> findStoreOrderByPoint(BigDecimal lat,
    public Page<Store> findStoreOrderByPoint(BigDecimal lat,
                                             BigDecimal lng,
                                             String category,
                                             List<String> facility,
                                             Pageable pageable) {

        BooleanBuilder builder = locTwoPointAndConditions(lat, lng, category, facility);
// 직접 DTO를 조작
//        List<Store> resultList = queryFactory.selectFrom(store)
//                .where(builder)
//                .fetch();

//        List<StoreCardResponseDto> resultAvgList = resultList.stream()
//                .map(store -> {
//                    long count = user == null ? 0L : user.getBookmarkList().stream()
//                            .filter(b -> b.getUserId() == user && b.getStoreId() == store).count();
//                    double avg = Double.parseDouble(String.format(String.valueOf(store.getReviewList().stream()
//                            .collect(Collectors.averagingDouble(Review::getPoint))), "0.1f"));
//                    StoreCardResponseDto storeCardResponseDto = new StoreCardResponseDto(store, count > 0);
//                    storeCardResponseDto.setPointAvg(avg);
//                    return storeCardResponseDto;
//                }).sorted().collect(Collectors.toList());
//
//        return getStoreCardPaged(resultAvgList, pageable);

//  업데이트시 쿼리
        List<Store> updateResultList = queryFactory.selectFrom(store)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(store.pointAvg.desc())
                .fetch();
        int fetchCount = queryFactory.selectFrom(store).where(builder).fetch().size();
        return new PageImpl<>(updateResultList, pageable, fetchCount);
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

    private List<StoreCardResponseDto> getStoreCardPaged(List<StoreCardResponseDto> storeResultList, Pageable pageable) {
        int[] pagingInfo = QuerydslLocation.getStartEndPage(storeResultList, pageable);
        return storeResultList.subList(pagingInfo[0], pagingInfo[1]);
    }

    private List<Store> getStorePaged(List<Store> storeResultList, Pageable pageable) {
        int[] pagingInfo = QuerydslLocation.getStartEndPage(storeResultList, pageable);
        return storeResultList.subList(pagingInfo[0], pagingInfo[1]);
    }

}
