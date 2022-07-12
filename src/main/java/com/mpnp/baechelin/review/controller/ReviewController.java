package com.mpnp.baechelin.review.controller;

import com.mpnp.baechelin.review.dto.ReviewReqDTO;
import com.mpnp.baechelin.review.dto.ReviewResDTO;
import com.mpnp.baechelin.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /** 리뷰 작성 */
    @PostMapping("/review")
    public ReviewResDTO review(@RequestParam double point,
                               @RequestParam String comment,
                               @RequestParam int storeId,
                               @RequestParam String socialId,
                               @RequestParam(required = false) MultipartFile imageFile) throws IOException {

        ReviewReqDTO reviewReqDTO = new ReviewReqDTO(socialId, storeId, comment, point, imageFile);
        reviewReqDTO
                .builder()
                .point(point)
                .comment(comment)
                .storeId(storeId)
                .socialId(socialId)
                .imageFile(imageFile)
                .build();

        System.out.println(reviewReqDTO.toString());

        reviewService.review(reviewReqDTO);
        return null;
    }

    /** 리뷰 삭제 */
    @PostMapping("/reviewDelete")
    public ReviewResDTO reviewDelete(@RequestParam int reviewId,
                                     @RequestParam String socialId) throws IOException {

        reviewService.reviewDelete(reviewId);
        return null;
    }

    /** 리뷰 수정 */
    @PostMapping("/reviewUpdate")
    public ReviewResDTO reviewUpdate(@RequestParam double point,
                                     @RequestParam String comment,
                                     @RequestParam int storeId,
                                     @RequestParam String socialId,
                                     @RequestParam int reviewId,
                                     @RequestParam(required = false) MultipartFile imageFile) throws IOException {

        ReviewReqDTO reviewReqDTO = new ReviewReqDTO(socialId, storeId, comment, point, imageFile);
        reviewReqDTO
                .builder()
                .point(point)
                .comment(comment)
                .storeId(storeId)
                .socialId(socialId)
                .imageFile(imageFile)
                .reviewId(reviewId)
                .build();

        System.out.println(reviewReqDTO.toString());

        reviewService.reviewUpdate(reviewReqDTO);
        return null;
    }

}
