package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.repository.BookmarkRepository;
import com.mpnp.baechelin.common.QuerydslLocation;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.mpnp.baechelin.store.dto.StorePagedResponseDto;
import com.mpnp.baechelin.store.repository.StoreQueryRepository;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreQueryRepository storeQueryRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

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
    public StorePagedResponseDto getStoreInRange(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, String category, List<String> facility, Pageable pageable, String socialId) {
//    public List<StoreCardResponseDto> getStoreInRange(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, String category, List<String> facility, Pageable pageable, String socialId) {
        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);
        Page<Store> betweenLngLat = storeQueryRepository.findBetweenLngLat(latStart, latEnd, lngStart, lngEnd, category, facility, pageable);
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
    public StorePagedResponseDto getStoreInRangeMain(BigDecimal lat, BigDecimal lng, String category, List<String> facility, Pageable pageable, String socialId) {
        BigDecimal[] range = QuerydslLocation.getRange(lat, lng, 10);
        return getStoreInRange(range[0], range[1], range[2], range[3], category, facility, pageable, socialId);
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
        if (targetUser == null) {
            for (Store store : resultStoreList) {
                mappingResult.add(new StoreCardResponseDto(store, "N"));
            }
        } else {
            for (Store store : resultStoreList) {
                boolean isBookmark = bookmarkRepository.existsByStoreIdAndUserId(store, targetUser);
                mappingResult.add(new StoreCardResponseDto(store, isBookmark ? "Y" : "N"));
            }
        }
        return new StorePagedResponseDto(resultStoreList.hasNext(), mappingResult);
    }


    /**
     * @param targetUser      현재 접근하고 있는 유저
     * @param resultStoreList 업장 리스트
     * @return 접근하고 있는 유저가 보는 업장을 가공(북마크 등)하여 DTO로 리턴
     */
    private List<StoreCardResponseDto> getStoreCardResponseDtos(User targetUser, List<Store> resultStoreList) {
        List<StoreCardResponseDto> storeCardResponseList = new ArrayList<>();
        if (targetUser == null) {
            return resultStoreList.stream()
                    .map(store -> new StoreCardResponseDto(store, "N"))
                    .collect(Collectors.toList());
        } else {
            for (Store store : resultStoreList) {
                String isBookmark = "N";
                for (Bookmark bookmark : store.getBookmarkList()) {
                    if (bookmark.getStoreId().getId() == store.getId()
                            && bookmark.getUserId().getSocialId().equals(targetUser.getSocialId())) {
                        isBookmark = "Y";
                    }
                    storeCardResponseList.add(new StoreCardResponseDto(store, isBookmark));
                }
            }
        }
        return storeCardResponseList;
    }

    /**
     * 업장 상세 조회
     * @param storeId 업장 아이디
     * @param socialId 유저 social 아이디
     * @return 업장 상세 정보
     */
    public StoreCardResponseDto getStore(int storeId, String socialId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));

        if (socialId == null) {
            return new StoreCardResponseDto(store, "N");
        } else {
            String isBookmark = "N";
            for (Bookmark bookmark : store.getBookmarkList()) {
                if (bookmark.getStoreId().getId() == store.getId()
                        && bookmark.getUserId().getSocialId().equals(socialId)) {
                    isBookmark = "Y";
                    break;
                }
            }
            return new StoreCardResponseDto(store, isBookmark);
        }
    }

    /**
     * 시/도 (ex. 서울시, 대전광역시)의 시/군/구 리스트 조회
     * @param sido 시/도
     * @return 시/군/구 리스트
     */
    public Map<String, List<String>> getSigungu(String sido) {
        List<Store> storeList = storeRepository.findAll();

        Map<String, List<String>> result = new HashMap<>();
        List<String> sigungu = new ArrayList<>();

        for (Store store : storeList) {
            String addressSido = store.getAddress().split(" ")[0];
            String addressSigungu = store.getAddress().split(" ")[1];

            if (addressSido.equals(sido)) {
                sigungu.add(addressSigungu);
            }
        }

        result.put("sigungu", sigungu);

        return result;
    }

    public List<StoreCardResponseDto> searchStores(String sido, String sigungu, String keyword, String socialId, Pageable pageable) {
        List<Store> storeList = storeQueryRepository.searchStores(sido, sigungu, keyword, pageable);

        List<StoreCardResponseDto> result = new ArrayList<>();

        for (Store store : storeList) {
            if (socialId == null) {
                result.add(new StoreCardResponseDto(store, "N"));
            } else {
                String isBookmark = "N";
                for (Bookmark bookmark : store.getBookmarkList()) {
                    if (bookmark.getStoreId().getId() == store.getId()
                            && bookmark.getUserId().getSocialId().equals(socialId)) {
                        isBookmark = "Y";
                        break;
                    }
                }
                result.add(new StoreCardResponseDto(store, isBookmark));
            }
        }
        return result;
    }
}