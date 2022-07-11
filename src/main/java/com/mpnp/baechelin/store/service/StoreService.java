package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.repository.ReviewRepository;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreResponseDto;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    public List<StoreResponseDto> getStoreList() {
        List<Store> storeList = storeRepository.findAll();

        List<StoreResponseDto> storeResponseList = new ArrayList<>();

        for (Store store : storeList) {
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

            StoreResponseDto storeResponse = StoreResponseDto.builder()
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
            storeResponseList.add(storeResponse);
        }


        return storeResponseList;
    }
}
