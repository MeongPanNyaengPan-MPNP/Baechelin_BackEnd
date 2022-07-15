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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/review/{storeId}")
    public ResponseEntity<List<ReviewResponseDto>> getStoreReview(@PathVariable int storeId) {
        List<ReviewResponseDto> reviewList = reviewService.getReview(storeId);
        return new ResponseEntity<>(reviewList, HttpStatus.OK);
    }

    /** 리뷰 작성 */
    @PostMapping("/review")
    public ResponseEntity<?> review(@ModelAttribute ReviewRequestDto reviewRequestDto,
                                    @AuthenticationPrincipal User user) throws IOException {
        if(user==null){
            throw new IllegalArgumentException("해당하는 회원 정보가 없습니다.");
        }
        reviewService.review(reviewRequestDto, user.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    /** 리뷰 수정 */
//    @PatchMapping("/reviewUpdate/{reviewId}")
//    public ResponseEntity<?> reviewUpdate(@ModelAttribute ReviewRequestDto reviewRequestDto,
//                                          @AuthenticationPrincipal User user,
//                                          @PathVariable int reviewId) throws IOException {
//        if(user==null){
//            throw new IllegalArgumentException("해당하는 회원 정보가 없습니다.");
//        }
//        reviewService.reviewUpdate(reviewRequestDto, user.getUsername(), reviewId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    // TODO - 최근 등록한 리뷰 보여주기 - 로그인 불필요
    // 반경 넓히기
    @GetMapping("/recent-review")
    public List<ReviewMainResponseDto> recentReview(@RequestParam(required = false) BigDecimal lat,
                                                    @RequestParam(required = false) BigDecimal lng,
                                                    @RequestParam int limit) {
        return reviewService.getRecentReview(lat, lng, limit);
    }

}
