package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.repository.BookmarkRepository;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.dto.ReviewResponseDto;
import com.mpnp.baechelin.review.repository.ReviewRepository;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.mpnp.baechelin.store.dto.StorePagedResponseDto;
import com.mpnp.baechelin.store.dto.StoreResponseDto;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final StoreQueryRepository storeQueryRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    public List<StoreResponseDto> getStoreList() {
        List<Store> storeList = storeRepository.findAll();

        List<StoreResponseDto> storeResponseList = new ArrayList<>();

        for (Store store : storeList) {
            storeResponseList.add(storeToResDto(store));
        }

        return storeResponseList;
    }

    public StoreResponseDto storeToResDto(Store store) {
        List<Review> reviewList = reviewRepository.findAllByStoreId(store);

        double totalPoint = 0;

        double pointAvg = 0;
        if (reviewList.size() > 0) {
            for (Review review : reviewList) {
                totalPoint += review.getPoint();
            }

            pointAvg = Double.parseDouble(String.format("%.1f", totalPoint / reviewList.size()));
        } else {
            pointAvg = 0;
        }

        return StoreResponseDto.builder()
                .storeId(store.getId())
                .category(store.getCategory())
                .name(store.getName())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .address(store.getAddress())
                .elevator(store.getElevator())
                .toilet(store.getToilet())
                .parking(store.getParking())
                .phoneNumber(store.getPhoneNumber())
                .heightDifferent(store.getHeightDifferent())
                .approach(store.getApproach())
                .storeImgList(null)
                .pointAvg(pointAvg)
                .build();
    }

    public StorePagedResponseDto getStoreInRange(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, String category, List<String> facility, Pageable pageable, String socialId) {
//    public List<StoreCardResponseDto> getStoreInRange(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, String category, List<String> facility, Pageable pageable, String socialId) {
        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);
        Page<Store> betweenLngLat = storeQueryRepository.findBetweenLngLat(latStart, latEnd, lngStart, lngEnd, category, facility, pageable);
        // store  가져와서 dto 매핑
        return getStoreCardPagedResponseDtos(targetUser, betweenLngLat);
    }

//    public List<StoreCardResponseDto> getStoreInRangeHighPoint(BigDecimal lat, BigDecimal lng, String
    public StorePagedResponseDto getStoreInRangeHighPoint(BigDecimal lat, BigDecimal lng, String
            category, List<String> facility, Pageable pageable, String socialId) {
        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);
        Page<Store> resultList = storeQueryRepository.findStoreOrderByPoint(lat, lng, category, facility, pageable, targetUser);
        return getStoreCardPagedResponseDtos(targetUser, resultList);
    }

    public List<StoreCardResponseDto> getStoreInRangeHighBookmark(BigDecimal lat, BigDecimal lng, String
            category, List<String> facility, int limit, String socialId) {
        User targetUser = socialId == null ? null : userRepository.findBySocialId(socialId);
        List<Store> highBookmarkResultList = storeQueryRepository.findStoreOrderByBookmark(lat, lng, category, facility, limit);
        return getStoreCardResponseDtos(targetUser, highBookmarkResultList);
    }

    private StorePagedResponseDto getStoreCardPagedResponseDtos(User targetUser, Page<Store> resultStoreList) {
        List<StoreCardResponseDto> mappingResult = new ArrayList<>();
        if (targetUser == null) {
            for (Store store : resultStoreList) {
                mappingResult.add(new StoreCardResponseDto(store, false));
            }
        } else {
            for (Store store : resultStoreList) {
                boolean isBookmark = bookmarkRepository.existsByStoreIdAndUserId(store, targetUser);
                mappingResult.add(new StoreCardResponseDto(store, isBookmark));
            }
        }
        return new StorePagedResponseDto(resultStoreList.hasNext(), mappingResult);
    }


    private List<StoreCardResponseDto> getStoreCardResponseDtos(User targetUser, List<Store> resultStoreList) {
        if (targetUser == null) {
            return resultStoreList.parallelStream().map(store -> new StoreCardResponseDto(store, false))
                    .collect(Collectors.toList());
        } else {
            return resultStoreList.parallelStream().map(store -> {
                long count = targetUser.getBookmarkList().stream()
                        .filter(b -> b.getUserId() == targetUser && b.getStoreId() == store).count();
                return new StoreCardResponseDto(store, count > 0);
            }).collect(Collectors.toList());// 순서보장}
        }
    }
}