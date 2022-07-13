package com.mpnp.baechelin.review.service;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.dto.ReviewRequestDto;
import com.mpnp.baechelin.review.repository.ReviewRepository;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.tag.domain.Tag;
import com.mpnp.baechelin.tag.repository.TagRepository;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import com.mpnp.baechelin.util.AwsS3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final TagRepository tagRepository;
    private final AwsS3Manager awsS3Manager;

    /**
     * 리뷰작성
     */
    public void review(ReviewRequestDto reviewRequestDto, String socialId) throws IOException {

        int storeId = reviewRequestDto.getStoreId();    //리뷰 업장id
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));
        User user = userRepository.findBySocialId(socialId);


        String reviewImageUrl = awsS3Manager.uploadFile(reviewRequestDto.getImageFile());
        Review review = new Review(reviewRequestDto, store, user, reviewImageUrl);
        reviewRepository.save(review);
        storeRepository.save(store.updatePointAvg(reviewRequestDto.getPoint()));

        List<Tag> tagList = new ArrayList<>();
        for (String s : reviewRequestDto.getTagList()) {
            // 확인해보기! - review
            tagList.add(Tag.builder().reviewId(review).tag(s).build()); //리뷰 태그
        }
        tagRepository.saveAll(tagList);

    }
}
