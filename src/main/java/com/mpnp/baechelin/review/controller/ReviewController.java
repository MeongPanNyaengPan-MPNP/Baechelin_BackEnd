package com.mpnp.baechelin.review.controller;

import com.mpnp.baechelin.review.dto.ReviewMainResDto;
import com.mpnp.baechelin.review.dto.ReviewReqDTO;
import com.mpnp.baechelin.review.dto.ReviewResDTO;
import com.mpnp.baechelin.review.repository.ReviewQueryRepository;
import com.mpnp.baechelin.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewQueryRepository reviewQueryRepository;

    /**
     * 리뷰 작성
     */
    @PostMapping("/review")
    public ReviewResDTO review(@RequestParam double point,
                               @RequestParam String comment,
                               @RequestParam int storeId,
                               @RequestParam int userId,
                               @RequestParam(required = false) MultipartFile imageFile) throws IOException {

        ReviewReqDTO reviewReqDTO = ReviewReqDTO.builder()
                .point(point)
                .comment(comment)
                .storeId(storeId)
                .userId(userId)
                .imageFile(imageFile)
                .build();

        reviewService.review(reviewReqDTO);
        return null;
    }

    // TODO - 최근 등록한 리뷰 보여주기
    // 반경 넓히기
    @GetMapping("/recent-review")
    public List<ReviewMainResDto> recentReview(@RequestParam(required = false) BigDecimal lat,
                                               @RequestParam(required = false) BigDecimal lng,
                                               @RequestParam int limit) {

        BigDecimal[] locationRange = getRange(lat, lng, 20);


        List<ReviewMainResDto> result = reviewQueryRepository
                .findRecentReviews(locationRange[0], locationRange[1], locationRange[2], locationRange[3], limit)
                .stream().map(ReviewMainResDto::new).collect(Collectors.toList());
        return result;
    }

    // km : 반경
    private BigDecimal[] getRange(BigDecimal lat, BigDecimal lng, int km) {
        // km->lat,lng로 변환하기
        final BigDecimal latitude = BigDecimal.valueOf(km / 110.569); // 반경
        final BigDecimal longitutde = BigDecimal.valueOf(km / 111.322);
        // 남서, 북동으로 받아오기
        // start lat-lng, end lat-lng으로 Array 받아오기
        return new BigDecimal[]{lat.subtract(latitude), lat.add(latitude),
                lng.subtract(longitutde), lng.add(longitutde)};
    }

}
