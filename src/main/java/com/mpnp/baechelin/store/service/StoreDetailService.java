package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.dto.ReviewResponseDto;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.domain.StoreImage;
import com.mpnp.baechelin.store.dto.StoreImgResponseDto;
import com.mpnp.baechelin.store.dto.StoreResponseDto;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreDetailService {

    private final StoreRepository storeRepository;

    /**
     * 업장 상세 조회
     * @param storeId
     * @param user
     * @return
     */
    public StoreResponseDto getStore(int storeId, User user) {
        Store foundStore = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));

        return storeToResDto(foundStore, user);
    }

    public StoreResponseDto storeToResDto(Store store, User user) {
        // StoreImage entity -> dto
        List<StoreImgResponseDto> storeImageList = new ArrayList<>();
        for (StoreImage storeImage : store.getStoreImageList()) {
            StoreImgResponseDto storeImgResponse = new StoreImgResponseDto(storeImage.getStoreImageUrl());
            storeImageList.add(storeImgResponse);
        }

        List<Review> reviewList = store.getReviewList();

        // List<Review> -> List<ReviewResponseDto>
        List<ReviewResponseDto> reviewResponseList = new ArrayList<>();

        double totalPoint = 0;
        double pointAvg;
        if (reviewList.size() > 0) {
            for (Review review : reviewList) {
                totalPoint += review.getPoint();

                // Review entity -> dto
                ReviewResponseDto reviewResponse = new ReviewResponseDto(review);
                reviewResponseList.add(reviewResponse);
            }
            // 소숫점 첫째자리 수까지 별점 평균 구하기
            pointAvg = Double.parseDouble(String.format("%.1f", totalPoint / reviewList.size()));
        } else {
            // review가 없다면 별점 평균 0
            pointAvg = 0;
        }

//        // TODO 북마크 여부 가져오기
//        //
//        for (Bookmark bookmark : store.getBookmarkList()) {
//
//        }
//        if (user == null) {
//
//        } else {
//
//        }


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
                .storeImgList(storeImageList)
                .pointAvg(pointAvg)
                .IsBookmark(null)
                .build();
    }

}
