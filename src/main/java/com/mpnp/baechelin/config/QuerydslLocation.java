package com.mpnp.baechelin.config;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static com.mpnp.baechelin.store.domain.QStore.store;

public class QuerydslLocation {
    public static BooleanBuilder locationBuilder(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(latStart == null ? null : store.latitude.goe(latStart));
        builder.and(latEnd == null ? null : store.latitude.loe(latEnd));
        builder.and(lngStart == null ? null : store.longitude.goe(lngStart));
        builder.and(lngEnd == null ? null : store.longitude.loe(lngEnd));
        return builder;
    }

    private static BooleanExpression facilityTF(String facility) {
        if (facility == null || facility.isEmpty()) return null;
        return givePath(facility).eq("Y");
    }

    private static StringPath givePath(String dbFacility) {
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

    public static BooleanBuilder locAndConditions(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, String category, List<String> facility) {
        BooleanBuilder builder = locationBuilder(latStart, latEnd, lngStart, lngEnd);
        return getBooleanBuilder(category, facility, builder);
    }


    public static BooleanBuilder locTwoPointAndConditions(BigDecimal latitude, BigDecimal longitude, String category, List<String> facility) {
        BooleanBuilder builder = new BooleanBuilder();
        if (latitude != null && longitude != null) {
            BigDecimal[] location = getRange(latitude, longitude, 10);
            builder = locationBuilder(location[0], location[1], location[2], location[3]);
        }
        return getBooleanBuilder(category, facility, builder);
    }

    public static BooleanBuilder getBooleanBuilder(String category, List<String> facility, BooleanBuilder builder) {
        builder.and(category == null ? null : store.category.eq(category));
        if (facility != null && facility.size() > 0) {
            for (String fac : facility) {
                builder.and(facilityTF(fac));
            }
        }
        return builder;
    }

    public static BigDecimal[] getRange(BigDecimal lat, BigDecimal lng, int km) {
        // km->lat,lng로 변환하기
        final BigDecimal latitude = BigDecimal.valueOf(km / 110.569); // 반경
        final BigDecimal longitude = BigDecimal.valueOf(km / 111.322);
        // 남서, 북동으로 받아오기
        // start lat-lng, end lat-lng으로 Array 받아오기
        return new BigDecimal[]{lat.subtract(latitude), lat.add(latitude),
                lng.subtract(longitude), lng.add(longitude)};
    }



    public static int[] getStartEndPage(List<?> resultList, Pageable pageable){
        int totalCount = resultList.size();
        int totalPage = totalCount / pageable.getPageSize();
        int pageStartIndex = Long.valueOf(resultList.size() / pageable.getPageSize() * pageable.getOffset()).intValue();

        // index 처리하기
        int start = 0, end = resultList.size();
        start = Math.max(start, pageStartIndex);
        end = Math.min(end, pageStartIndex + pageable.getPageSize() - 1);
        return new int[]{start, end};
    }
}
