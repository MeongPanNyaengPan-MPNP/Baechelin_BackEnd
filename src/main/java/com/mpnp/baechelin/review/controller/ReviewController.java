package com.mpnp.baechelin.review.controller;

import com.mpnp.baechelin.review.dto.ReviewReqDTO;
import com.mpnp.baechelin.review.dto.ReviewResDTO;
import com.mpnp.baechelin.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**리뷰 작성*/
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
}
