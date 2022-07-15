package com.mpnp.baechelin.review.service;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.domain.ReviewImage;
import com.mpnp.baechelin.review.dto.ReviewMainResponseDto;
import com.mpnp.baechelin.review.dto.ReviewRequestDto;
import com.mpnp.baechelin.review.dto.ReviewResponseDto;
import com.mpnp.baechelin.review.repository.ReviewImageRepository;
import com.mpnp.baechelin.review.repository.ReviewQueryRepository;
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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final TagRepository tagRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final AwsS3Manager awsS3Manager;

    /**
     * 리뷰작성
     */
    public void review(ReviewRequestDto reviewRequestDto, String socialId) throws IOException {

        int storeId = reviewRequestDto.getStoreId();    //리뷰 업장id
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));
        User user = userRepository.findBySocialId(socialId);

        Review review = new Review(reviewRequestDto, store, user);

        List<Tag> tagList = new ArrayList<>();
        for (String s : reviewRequestDto.getTagList()) {
            tagList.add(new Tag(s, review));
        } // 태그 -> 엔티티 변환

        List<ReviewImage> reviewImageUrlList = new ArrayList<>();
        for (MultipartFile reviewImage : reviewRequestDto.getImageFile()) {
            reviewImageUrlList.add(ReviewImage.builder().reviewId(review).reviewImageUrl(awsS3Manager.uploadFile(reviewImage)).build());
        } // 리뷰이미지 -> url -> 엔티티 변환

        tagRepository.saveAll(tagList);
        reviewImageRepository.saveAll(reviewImageUrlList);

        reviewRepository.save(review);

        storeRepository.save(store.updatePointAvg(reviewRequestDto.getPoint()));
    }

    public List<ReviewResponseDto> getReview(int storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다"));
        return reviewRepository.findAllByStoreId(store)
                .stream().map(ReviewResponseDto::new).collect(Collectors.toList());
    }

    public void reviewUpdate(ReviewRequestDto reviewRequestDto, String socialId, int reviewId) throws IOException {
        int storeId = reviewRequestDto.getStoreId();
        User user = userRepository.findBySocialId(socialId);
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));

        // 리뷰이미지 변환
        List<ReviewImage> reviewImageUrlList = new ArrayList<>();
        List<MultipartFile> imageFileList = reviewRequestDto.getImageFile();
//        awsS3Manager.deleteFile();

        for (MultipartFile reviewImage : imageFileList) {
            reviewImageUrlList.add(ReviewImage.builder().reviewImageUrl(awsS3Manager.uploadFile(reviewImage)).build());
        }

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("해당하는 리뷰가 없습니다."));
        List<String> tagList = reviewRequestDto.getTagList();

        review.update(reviewRequestDto);
        for (String tagObj : tagList) {
            Tag tag = new Tag(tagObj, review);
            review.addSingleTag(tag);
            tagRepository.save(tag);
        }

        reviewRepository.save(review);
        storeRepository.save(store.updatePointAvg(reviewRequestDto.getPoint()));
    }

    public List<ReviewMainResponseDto> getRecentReview(BigDecimal lat, BigDecimal lng, int limit) {
        return reviewQueryRepository
                .findRecentReviews(lat, lng, limit)
                .parallelStream().map(review -> new ReviewMainResponseDto(review, review.getStoreId(), review.getUserId())).collect(Collectors.toList());
    }

}
