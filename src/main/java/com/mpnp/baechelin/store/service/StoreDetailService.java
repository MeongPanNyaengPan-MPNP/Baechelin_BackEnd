package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.dto.ReviewResDTO;
import com.mpnp.baechelin.review.repository.ReviewRepository;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreResponseDto;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreDetailService {

    private final StoreRepository storeRepository;


    public StoreResponseDto getStore(int storeId) {
        Store foundStore = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));

        // 북마크 여부, 리뷰 리스트 들어가야함.
        return storeToResDto(foundStore);
    }

    // store entity -> store dto (로직 합쳐지면 삭제 예정)
    public StoreResponseDto storeToResDto(Store store){
        List<Review> reviewList = store.getReviewList();

        double totalPoint = 0;
        double pointAvg = 0;
        if (reviewList.size() > 0) {
            for (Review review : reviewList) {
                // 별점 평균 구하기
                totalPoint += review.getPoint();
            }

            pointAvg = Double.parseDouble(String.format("%.1f", totalPoint / reviewList.size()));
        } else {
            pointAvg = 0;
        }

        // 북마크 여부 가져오기
//        List<Bookmark> bookmarkList = store.getBookmarkList();
//        for (Bookmark bookmark : bookmarkList) {
//
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
                .storeImgList(null)
                .pointAvg(pointAvg)
//                .reviewList() // 리뷰 리스트 리턴해야함 DTO...
                .build();
    }

//    public ReviewResDTO reviewToResDto(Review review) {
//        Tag tag = review.getTagId();
//
//        ReviewResDTO.builder()
//                .reviewId(review.getId())
//                .review(review.getReview())
//                .point(review.getPoint())
//                .reviewImageUrl(review.getReviewImageUrl())
//                .bKiosk(tag.getBKiosk())
//                .bTable(review.getTagId().getBTable())
//    }
}
