package com.mpnp.baechelin.review.service;

import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.domain.ReviewImage;
import com.mpnp.baechelin.review.dto.ReviewRequestDto;
import com.mpnp.baechelin.review.dto.ReviewResponseDto;
import com.mpnp.baechelin.review.repository.ReviewImageRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final AwsS3Manager awsS3Manager;

    /** 리뷰 작성 */
    public void review(ReviewRequestDto reviewRequestDto, String socialId) throws IOException {

        int    storeId = reviewRequestDto.getStoreId();    //리뷰 업장 ID
        Store  store   = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));
        User   user    = userRepository.findBySocialId(socialId);
        Review review  = new Review(reviewRequestDto, store, user);

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

    /** 리뷰 수정 */
    public void reviewUpdate(ReviewRequestDto reviewRequestDto, String socialId, int reviewId) throws IOException {

//        int   storeId  = reviewRequestDto.getStoreId();
//        User     user  = userRepository.findBySocialId(socialId); if(user == null){ new IllegalArgumentException("해당하는 소셜아이디를 찾을 수 없습니다."); }
//        Store   store  = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));
//        Review review  = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("해당하는 리뷰가 없습니다."));
//
//
//        // 리뷰이미지 변환
//        List<MultipartFile>    imageFileList = reviewRequestDto.getImageFile();
//        List<ReviewImage> reviewImageUrlList = new ArrayList<>();
//
//        if(imageFileList != null && review.getReviewImageList() != null) {
//            int index = 0;
//            for (MultipartFile reviewImage : imageFileList) {
//                awsS3Manager.deleteFile(review.getReviewImageList().get(index).getReviewImageUrl());
//                reviewImageUrlList.add(ReviewImage.builder().reviewImageUrl(awsS3Manager.uploadFile(reviewImage)).build());
//                index++;
//            }
//        } else if(imageFileList != null && review.getReviewImageList() != null){
//
//        }
//
//
//        review.update(reviewRequestDto);
//        List<Tag>       tags = new ArrayList<>();
//        List<String> tagList = reviewRequestDto.getTagList();
//
//
//        if (tagList != null) {
//            for (String tagObj : tagList) {
//                Tag tag = new Tag(tagObj, review);
//                review.addSingleTag(tag);
//                tags.add(tag);
//            }
//            tagRepository.saveAll(tags);
//        }
//
//        reviewRepository.save(review); // 아래의 {store.updatePointAvg()} 보다 리뷰가 먼저 처리되게 해야한다.
//        storeRepository .save(store.updatePointAvg(reviewRequestDto.getPoint()));
    }

    /** 리뷰 삭제 */
    public void reviewDelete(String socialId, int reviewId) {


        User                   user = userRepository.findBySocialId(socialId);
        Optional<Review>     review = reviewRepository.findById(reviewId);
        List<ReviewImage> imageList = review.get().getReviewImageList();


        // 1.유저 유무 예외처리 -> 2.리뷰 유무 예외처리
        if(user == null){ new IllegalArgumentException("해당하는 소셜아이디를 찾을 수 없습니다."); }
        review.orElseThrow(() -> new IllegalArgumentException("해당하는 리뷰가 이미 삭제 되었습니다."));


        // 1.리뷰삭제 -> 2.이미지 삭제
        reviewRepository.deleteById(review.get().getId());
        if(!review.get().getReviewImageList().isEmpty()) {
            for (ReviewImage reviewImage : imageList) {
                System.out.println(reviewImage.getReviewImageUrl().substring(reviewImage.getReviewImageUrl().indexOf("com/")+4));
                awsS3Manager.deleteFile(reviewImage.getReviewImageUrl().substring(reviewImage.getReviewImageUrl().indexOf("com/")+4));
            }
        }
    }
}
