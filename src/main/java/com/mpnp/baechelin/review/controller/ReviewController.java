package com.mpnp.baechelin.review.controller;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.dto.PageInfoResponseDto;
import com.mpnp.baechelin.review.dto.ReviewMainResponseDto;
import com.mpnp.baechelin.review.dto.ReviewRequestDto;
import com.mpnp.baechelin.review.dto.ReviewResponseDto;
import com.mpnp.baechelin.review.repository.ReviewQueryRepository;
import com.mpnp.baechelin.review.repository.ReviewRepository;
import com.mpnp.baechelin.review.service.ReviewService;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    /** 리뷰 조회 */
    @GetMapping("/review/{storeId}")
    public ResponseEntity<PageInfoResponseDto> getStoreReview(@PathVariable int storeId,
                                                              @AuthenticationPrincipal User user,
                                                              @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        PageInfoResponseDto pageInfoResponseDto = reviewService.getReview(storeId, user == null ? null : user.getUsername(), pageable);
        return new ResponseEntity<>(pageInfoResponseDto, HttpStatus.OK);
    }

//    @GetMapping("/review/{storeId}")
//    public ResponseEntity<List<ReviewResponseDto>> getStoreReview(@PathVariable int storeId,
//                                                                  @AuthenticationPrincipal User user) {
//        List<ReviewResponseDto> reviewList = reviewService.getReview(storeId, user.getUsername());
//        return new ResponseEntity<>(reviewList, HttpStatus.OK);
//    }

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

    @PostMapping("/review/imageUpload")
    public ResponseEntity<?> imageUpload(@AuthenticationPrincipal User user,
                                         @RequestParam MultipartFile imageFile){

        if(user==null){ throw new IllegalArgumentException("해당하는 회원 정보가 없습니다."); }

        reviewService.imageUpload(imageFile, user.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** 리뷰 삭제 */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<?> reviewDelete(@AuthenticationPrincipal User user,
                                          @PathVariable int reviewId) {

        if(user==null){ throw new IllegalArgumentException("해당하는 회원 정보가 없습니다."); }
        reviewService.reviewDelete(user.getUsername(), reviewId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 반경 넓히기
    @GetMapping("/recent-review")
    public List<ReviewMainResponseDto> recentReview(@RequestParam(required = false) BigDecimal lat,
                                                    @RequestParam(required = false) BigDecimal lng,
                                                    @RequestParam int limit) {
        return reviewService.getRecentReview(lat, lng, limit);
    }

}
