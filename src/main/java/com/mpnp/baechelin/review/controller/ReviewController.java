package com.mpnp.baechelin.review.controller;

import com.mpnp.baechelin.common.SuccessResponse;
import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.exception.ErrorResponse;
import com.mpnp.baechelin.review.dto.PageInfoResponseDto;
import com.mpnp.baechelin.review.dto.ReviewMainResponseDto;
import com.mpnp.baechelin.review.dto.ReviewRequestDto;
import com.mpnp.baechelin.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 조회
     */
    @GetMapping("/review/{storeId}")
    public ResponseEntity<PageInfoResponseDto> getStoreReview(@PathVariable int storeId,
                                                              @AuthenticationPrincipal User user,
                                                              @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        if (user == null) {
            PageInfoResponseDto pageInfoResponseDto = reviewService.getReview(storeId, pageable);
            return new ResponseEntity<>(pageInfoResponseDto, HttpStatus.OK);
        }
        PageInfoResponseDto pageInfoResponseDto = reviewService.getReview(storeId, user.getUsername(), pageable);
        return new ResponseEntity<>(pageInfoResponseDto, HttpStatus.OK);
    }

//    @GetMapping("/review/{storeId}")
//    public ResponseEntity<List<ReviewResponseDto>> getStoreReview(@PathVariable int storeId,
//                                                                  @AuthenticationPrincipal User user) {
//        List<ReviewResponseDto> reviewList = reviewService.getReview(storeId, user.getUsername());
//        return new ResponseEntity<>(reviewList, HttpStatus.OK);
//    }

    /**
     * 리뷰 작성
     */
    @PostMapping("/review")
    public ResponseEntity<?> review(@ModelAttribute @Valid ReviewRequestDto reviewRequestDto,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal User user) throws IOException {
        if (user == null) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        reviewService.review(reviewRequestDto, user.getUsername());
        return ResponseEntity.ok("리뷰 등록 성공");
    }

    /**
     * 리뷰 수정
     */
    @PatchMapping("/review/{reviewId}")
    public ResponseEntity<?> reviewUpdate(@ModelAttribute @Valid ReviewRequestDto reviewRequestDto,
                                               BindingResult bindingResult,
                                               @AuthenticationPrincipal User user,
                                               @PathVariable int reviewId) {
        if (user == null) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        reviewService.reviewUpdate(reviewRequestDto, user.getUsername(), reviewId);
        return ResponseEntity.ok("리뷰 수정 성공");
    }


//    @PostMapping("/review/imageUpload")
//    public SuccessResponse imageUpload(@AuthenticationPrincipal User user,
//                                       @RequestParam MultipartFile imageFile) {
//
//        if (user == null) {
//            throw new CustomException(ErrorCode.ACCESS_DENIED);
//        }
//        reviewService.imageUpload(imageFile, user.getUsername());
//        return new SuccessResponse("리뷰 이미지 등록 완료");
//    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<?> reviewDelete(@AuthenticationPrincipal User user,
                                        @PathVariable int reviewId) {

        if (user == null) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        reviewService.reviewDelete(user.getUsername(), reviewId);
        return ResponseEntity.ok("리뷰 삭제 성공");
    }

    // 반경 넓히기
    @GetMapping("/recent-review")
    public List<ReviewMainResponseDto> recentReview(@RequestParam(required = false) BigDecimal lat,
                                                    @RequestParam(required = false) BigDecimal lng,
                                                    @RequestParam int limit) {
        return reviewService.getRecentReview(lat, lng, limit);
    }
}
