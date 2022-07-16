package com.mpnp.baechelin.review.controller;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.dto.ReviewMainResponseDto;
import com.mpnp.baechelin.review.dto.ReviewRequestDto;
import com.mpnp.baechelin.review.dto.ReviewResponseDto;
import com.mpnp.baechelin.review.repository.ReviewQueryRepository;
import com.mpnp.baechelin.review.repository.ReviewRepository;
import com.mpnp.baechelin.review.service.ReviewService;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewQueryRepository reviewQueryRepository;

    @GetMapping("/review/{storeId}")
    public ResponseEntity<List<ReviewResponseDto>> getStoreReview(@PathVariable int storeId) {
        List<ReviewResponseDto> reviewList = reviewService.getReview(storeId);
        return new ResponseEntity<>(reviewList, HttpStatus.OK);
    }

    /** 리뷰 작성 */
    @PostMapping("/review")
    public ResponseEntity<?> review(@ModelAttribute ReviewRequestDto reviewRequestDto,
                                    @AuthenticationPrincipal User user) throws IOException {


        if(user==null){ throw new IllegalArgumentException("해당하는 회원 정보가 없습니다.");}
        reviewService.review(reviewRequestDto, user.getUsername());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** 리뷰 수정 */
    @PatchMapping("/review/{reviewId}")
    public ResponseEntity<?> reviewUpdate(@ModelAttribute ReviewRequestDto reviewRequestDto,
                                          @AuthenticationPrincipal User user,
                                          @PathVariable int reviewId) throws IOException {

        if(user==null){ throw new IllegalArgumentException("해당하는 회원 정보가 없습니다."); }
        reviewService.reviewUpdate(reviewRequestDto, user.getUsername(), reviewId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** 리뷰 삭제 */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<?> reviewDelete(@AuthenticationPrincipal User user,
                                          @PathVariable int reviewId) throws IOException {

        if(user==null){ throw new IllegalArgumentException("해당하는 회원 정보가 없습니다."); }
        reviewService.reviewDelete(user.getUsername(), reviewId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO - 최근 등록한 리뷰 보여주기
    // 반경 넓히기
    @GetMapping("/recent-review")
    public List<ReviewMainResponseDto> recentReview(@RequestParam(required = false) BigDecimal lat,
                                                    @RequestParam(required = false) BigDecimal lng,
                                                    @RequestParam int limit) {

        BigDecimal[] locationRange = getRange(lat, lng, 20);


        List<ReviewMainResponseDto> result = reviewQueryRepository
                .findRecentReviews(locationRange[0], locationRange[1], locationRange[2], locationRange[3], limit)
                .stream().map(ReviewMainResponseDto::new).collect(Collectors.toList());
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
