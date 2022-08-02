package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.bookmark.repository.BookmarkRepository;
import com.mpnp.baechelin.common.QuerydslLocation;
import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.mpnp.baechelin.store.dto.StoreDetailResponseDto;
import com.mpnp.baechelin.store.dto.StorePagedResponseDto;
import com.mpnp.baechelin.store.repository.StoreQueryRepository;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10S")
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreQueryRepository storeQueryRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 위도, 경도 두 개를 받아와서 시설, 카테고리에 해당하는 업장을 필터링하는 메서드
     *
     * @param latStart 남서쪽 위도
     * @param latEnd   북동쪽 위도
     * @param lngStart 남서쪽 경도
     * @param lngEnd   북동쪽 경도
     * @param category 업장 카테고리
     * @param facility 배리어 프리 태그
     * @param pageable 페이징 요소
     * @param socialId 유저 소셜 로그인 아이디
     * @return 조건을 만족하는 업장의 DTO
     */

    public StorePagedResponseDto getStoreInTwoPointRange(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, String category, List<String> facility, Pageable pageable, String socialId) {
//    public List<StoreCardResponseDto> getStoreInRange(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, String category, List<String> facility, Pageable pageable, String socialId) {
        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);
        Page<Store> betweenLngLat = storeQueryRepository.findBetweenTwoPointOrder(latStart, latEnd, lngStart, lngEnd, category, facility, pageable);
        // store  가져와서 dto 매핑
        return getStoreCardPagedResponseDto(targetUser, betweenLngLat);
    }

    public StorePagedResponseDto getStoreInOnePointRange(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, BigDecimal lat, BigDecimal lng,
                                                         String category, List<String> facility, Pageable pageable, String socialId) {
//    public List<StoreCardResponseDto> getStoreInRange(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, String category, List<String> facility, Pageable pageable, String socialId) {
        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);
        Page<Store> betweenLngLat = storeQueryRepository.findBetweenOnePointOrder(latStart, latEnd, lngStart, lngEnd, lat, lng, category, facility, pageable);
        // store  가져와서 dto 매핑
        return getStoreCardPagedResponseDto(targetUser, betweenLngLat);
    }


    /**
     * @param lat      위도
     * @param lng      경도
     * @param category 업장 카테고리
     * @param facility 배리어 프리 태그
     * @param pageable 페이징 요소
     * @param socialId 유저 소셜 로그인 아이디
     * @return 위도, 경도, 카테고리, 배리어 프리, 페이징을 만족하는 배리어 프리 업장 리턴
     */
    public StorePagedResponseDto getStoreInOnePointRange(BigDecimal lat, BigDecimal lng, String category, List<String> facility, Pageable pageable, String socialId) {
        BigDecimal[] range = QuerydslLocation.getRange(lat, lng, 2);
        if (range == null)
            return getStoreInOnePointRange(null, null, null, null,lat,lng, category, facility, pageable, socialId);
        return getStoreInOnePointRange(range[0], range[1], range[2], range[3],lat,lng, category, facility, pageable, socialId);
    }

    public StorePagedResponseDto getStoreInRangeMap(BigDecimal lat, BigDecimal lng, String category, List<String> facility, Pageable pageable, String socialId) {
        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);
        Page<Store> betweenLngLat = storeQueryRepository.findStoreOrderByPoint(lat, lng, category, facility, pageable);
        // store  가져와서 dto 매핑
        return getStoreCardPagedResponseDto(targetUser, betweenLngLat);
    }

    /**
     * @param lat      위도
     * @param lng      경도
     * @param category 업장 카테고리
     * @param facility 배리어 프리 태그
     * @param pageable 페이징 요소
     * @param socialId 유저 소셜 로그인 아이디
     * @return 페이징이 적용된 높은 별점 순으로 정렬된 업장 리스트 리턴
     */
    //    public List<StoreCardResponseDto> getStoreInRangeHighPoint(BigDecimal lat, BigDecimal lng, String
    public StorePagedResponseDto getStoreInRangeHighPoint(BigDecimal lat, BigDecimal lng, String
            category, List<String> facility, Pageable pageable, String socialId) {
        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);
        Page<Store> pagedResultList = storeQueryRepository.findStoreOrderByPoint(lat, lng, category, facility, pageable);
        return getStoreCardPagedResponseDto(targetUser, pagedResultList);
    }

    /**
     * @param lat      위도
     * @param lng      경도
     * @param category 업장 카테고리
     * @param facility 배리어 프리 태그
     * @param socialId 유저 소셜 아이디
     * @return 위도, 경도, 카테고리, 배리어 프리 태그에 해당하는 북마크가 높은 업장 리스트를 설정한 숫자만큼 리턴
     */
    public StorePagedResponseDto getStoreInRangeHighBookmark(BigDecimal lat, BigDecimal lng, String
            category, List<String> facility, Pageable pageable, String socialId) {
        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);
        Page<Store> highBookmarkResultList = storeQueryRepository.findStoreOrderByBookmark(lat, lng, category, facility, pageable);
        return getStoreCardPagedResponseDto(targetUser, highBookmarkResultList);
    }

    /**
     * @param targetUser      현재 접근하고 있는 유저
     * @param resultStoreList 업장 리스트
     * @return 접근하고 있는 유저가 보는 페이징된 업장을 가공(북마크 등)하여 DTO로 리턴
     */
    private StorePagedResponseDto getStoreCardPagedResponseDto(User targetUser, Page<Store> resultStoreList) {
        List<StoreCardResponseDto> mappingResult = new ArrayList<>();
        for (Store store : resultStoreList) {
            boolean isBookmark = targetUser != null && bookmarkRepository.existsByStoreIdAndUserId(store, targetUser);
            mappingResult.add(new StoreCardResponseDto(store, isBookmark ? "Y" : "N"));
        }
        return new StorePagedResponseDto(resultStoreList, mappingResult);
    }


    /**
     * 업장 상세 조회
     *
     * @param storeId  업장 아이디
     * @param socialId 유저 social 아이디
     * @return 업장 상세 정보
     */

    public StoreDetailResponseDto getStore(long storeId, String socialId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new CustomException(ErrorCode.NO_STORE_FOUND));

        List<String> storeImageList = new ArrayList<>();

        store.getStoreImageList().forEach(storeImage -> storeImageList.add(storeImage.getStoreImageUrl()));
        store.getReviewList().forEach(review -> review.getReviewImageList()
                .forEach(reviewImage -> storeImageList.add(reviewImage.getReviewImageUrl())));

        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);

        boolean isBookmark = bookmarkRepository.existsByStoreIdAndUserId(store, targetUser);
        return new StoreDetailResponseDto(store, isBookmark ? "Y" : "N", storeImageList);
    }

    /**
     * 시/도 (ex. 서울시, 대전광역시)의 시/군/구 리스트 조회
     *
     * @param sido 시/도
     * @return json 형태의 시/군/구 리스트
     */
    public Map<String, List<String>> getSigungu(String sido) {
        // FullText Search
        List<Store> foundAddress = storeQueryRepository.getSigungu(sido);

        // 결과를 json 형태로 리턴
        Map<String, List<String>> result = new HashMap<>();
        // 중복 제거를 위해 Set 생성
        Set<String> sigunguSet = new HashSet<>();

        for (Store store : foundAddress) {
            String[] address = store.getAddress().split(" "); // [0] : 시/도, [1] : 시/군/구, [2] : 구

            // 인자값인 sido와 주소의 첫번째 "시"가 같을 때
            if (address[0].contains(sido)) {
                // [경기도 성남시 분당구]처럼 도 - 시 - 구 로 나눠지는 경우 시 + 구로 반환
                if (address[2].charAt(address[2].length() - 1) == '구') {
                    sigunguSet.add(address[1] + " " + address[2]);
                } else {
                    sigunguSet.add(address[1]);
                }
            }
        }

        // 정렬을 위해 Set -> List로 변환
        List<String> sigungu = new ArrayList<>(sigunguSet);
        // List 정렬
        Collections.sort(sigungu);

        result.put("sigungu", sigungu);

        return result;
    }

    /**
     * 업장 검색
     *
     * @param sido     시/도명
     * @param sigungu  시/군/구명
     * @param keyword  검색어
     * @param category 카테고리
     * @param facility 배리어 프리 시설
     * @param socialId 사용자 소셜 아이디
     * @param pageable 페이징
     * @return 페이징이 적용된 검색 결과 리턴
     */
    public StorePagedResponseDto searchStores(String sido, String sigungu, String keyword, String category, List<String> facility, String socialId, Pageable pageable) {
        Page<Store> searchStores = storeQueryRepository.searchStores(sido, sigungu, keyword, category, facility, pageable);

        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);

        return getStoreCardPagedResponseDto(targetUser, searchStores);
    }

    @Scheduled(cron = "0 0 0-23 * * *") // 1시간에 한 번
    @SchedulerLock(name = "updateScheduler", lockAtLeastFor = "PT50M", lockAtMostFor = "PT58M")
    public void updateSchedule() {
        log.info("AVG, BOOKMARK COUNT SCHEDULING");
        List<Store> storeList = storeRepository.findAll();
        for (Store store : storeList) {
            if (!store.getReviewList().isEmpty()) {
                double storeAvg = Double.parseDouble(String.format("%.1f", storeRepository.getAvg(store.getId())));
                storeRepository.updateAvg(storeAvg, store.getId());
            }
            if (!store.getBookmarkList().isEmpty()) {
                int bookmarkCnt = storeRepository.getBookmarkCnt(store.getId());
                storeRepository.updateBookmarkCnt(bookmarkCnt, store.getId());
            }
        }
    }
}