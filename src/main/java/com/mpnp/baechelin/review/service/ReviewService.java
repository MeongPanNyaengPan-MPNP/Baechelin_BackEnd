package com.mpnp.baechelin.review.service;

import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.domain.ReviewImage;
import com.mpnp.baechelin.review.dto.PageInfoResponseDto;
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
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final AwsS3Manager awsS3Manager;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewQueryRepository reviewQueryRepository;


    /**
     * 리뷰 작성
     */
    public void review(ReviewRequestDto reviewRequestDto, String socialId) throws IOException {

        long storeId = reviewRequestDto.getStoreId();
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));
        User user = userRepository.findBySocialId(socialId);
        Review review = new Review(reviewRequestDto, store, user);


        // todo 태크 매핑
        List<Tag> tagList = new ArrayList<>();
        for (String s : reviewRequestDto.getTagList()) {
            System.out.println("tag --> " + s);
            tagList.add(new Tag(s, review));
        } // 태그 -> 엔티티 변환


        List<ReviewImage> reviewImageUrlList = new ArrayList<>();
        List<MultipartFile> newReviewImage = reviewRequestDto.getImageFile();


        // todo 이미지가 널값이 아니라면 업로드 실행
        if (newReviewImage != null && !newReviewImage.isEmpty()) {
            for (MultipartFile reviewImageFile : newReviewImage) {
                String fileDir = awsS3Manager.uploadFile(reviewImageFile);
                log.info("upload --> " + fileDir);
                reviewImageUrlList.add(ReviewImage.builder().reviewId(review).reviewImageUrl(fileDir).build());
            } // 리뷰이미지 -> url -> 엔티티 변환
        }


        tagRepository.saveAll(tagList);
        reviewImageRepository.saveAll(reviewImageUrlList);
        reviewRepository.save(review); // 아래의 {store.updatePointAvg()} 보다 리뷰가 먼저 처리되게 해야한다.
        storeRepository.save(store.updatePointAvg()); //별점 평균 구하는 코드
    }


    /**
     * 리뷰 조회
     */

    public PageInfoResponseDto getReview(long storeId, String socialId, Pageable pageable) {

        User myUser = userRepository.findBySocialId(socialId);
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new CustomException(ErrorCode.NO_STORE_FOUND));
        Page<Review> reviewList = reviewRepository.findAllByStoreId(store, pageable);


        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();
        for (Review review : reviewList) {
            ReviewResponseDto reviewResponseDto = new ReviewResponseDto(review);
            User user = userRepository.findById(reviewResponseDto.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.NO_USER_FOUND));
            reviewResponseDto.userInfo(user, myUser);
            reviewResponseDtoList.add(reviewResponseDto);
        }

        return PageInfoResponseDto
                .builder()
                .totalElements((int) reviewList.getTotalElements())
                .totalPages(reviewList.getTotalPages())
                .number(reviewList.getNumber())
                .size(reviewList.getSize())
                .reviewResponseDtoList(reviewResponseDtoList)
                .hasNextPage(!reviewList.isFirst())
                .hasPreviousPage(reviewList.isLast())
                .build();
    }

    public PageInfoResponseDto getReview(long storeId, Pageable pageable) {

        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다"));
        Page<Review> reviewList = reviewRepository.findAllByStoreId(store, pageable);


        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();
        for (Review review : reviewList) {
            ReviewResponseDto reviewResponseDto = new ReviewResponseDto(review);
            User user = userRepository.findById(reviewResponseDto.getUserId()).orElseThrow(()-> new CustomException(ErrorCode.NO_USER_FOUND));
            reviewResponseDto.userInfo(user);
            reviewResponseDtoList.add(reviewResponseDto);
        }


        return PageInfoResponseDto
                .builder()
                .totalElements((int) reviewList.getTotalElements())
                .totalPages(reviewList.getTotalPages())
                .number(reviewList.getNumber())
                .size(reviewList.getSize())
                .reviewResponseDtoList(reviewResponseDtoList)
                .hasNextPage(reviewList.isFirst())
                .hasPreviousPage(reviewList.isLast())
                .build();
    }


    @Transactional
    /** 리뷰 수정 */

    public void reviewUpdate(ReviewRequestDto reviewRequestDto, String socialId, int reviewId) {


    }

//    @Transactional
//    /** 리뷰 수정 */
//    public void reviewUpdate(ReviewRequestDto reviewRequestDto, String socialId, int reviewId){
//
//        long      storeId    = reviewRequestDto.getStoreId();
//        User      user       = userRepository.findBySocialId(socialId); if(user == null){ new IllegalArgumentException("해당하는 소셜아이디를 찾을 수 없습니다."); }
//        Store     store      = storeRepository.findById(storeId)       .orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));
//        Review    review     = reviewRepository.findById(reviewId)     .orElseThrow(() -> new IllegalArgumentException("해당하는 리뷰가 없습니다."));
//
//
//        List<MultipartFile> newImageFileList = reviewRequestDto.getImageFile(); // 새로운 이미지 파일
//        List<ReviewImage>   oldImageFileList = review.getReviewImageList();     // 기존에 이미지 파일
//        List<ReviewImage> reviewImageUrlList = new ArrayList<>();               // 이미지 파일을 담을 리스트
//
//        List<ReviewImage> reviewImageList = reviewImageRepository.findAllByReviewId(review);
//        List<Integer>          reviewImageIdList = new ArrayList<>();               //
//
//
//        // todo 이미지 삭제 후 수정 작업 (1 -> 2)
//        // 1.기존리뷰에 기존 이미지가 있다면 삭제
//        if(!oldImageFileList.isEmpty()) {
//            for (ReviewImage reviewImage : oldImageFileList) {
//                System.out.println("check -> "  + reviewImage.getReviewImageUrl());
//                System.out.println("delete -> " + reviewImage.getReviewImageUrl().substring(reviewImage.getReviewImageUrl().indexOf("com/") + 4));
//                awsS3Manager.deleteFile(reviewImage.getReviewImageUrl().substring(reviewImage.getReviewImageUrl().indexOf("com/") + 4));
//
//            }
//        }
//
//
//        // 2.수정할 이미지가 있다면 업로드
//        if(!newImageFileList.isEmpty()) {
//            System.out.println("newImageFileList != null");
//            for (MultipartFile reviewImageFile : newImageFileList) {
//                String fileDir = awsS3Manager.uploadFile(reviewImageFile);
//                System.out.println("update --> " + fileDir);
//                reviewImageUrlList.add(ReviewImage.builder().reviewId(review).reviewImageUrl(fileDir).build());
//            } // 리뷰이미지 -> url -> 엔티티 변환
//        }
//
//
//
//        List<Tag>       tagList = new ArrayList<>();               // 태그를 담는 리스트
//        List<String> newTagList = reviewRequestDto.getTagList();   // 새로운 태그
//
//
//        // todo 태그 수정 작업
//        List<Tag> oldTagList = tagRepository.findAllByReviewId(review);
//        System.out.println("log Tag SIZE --> "+ oldTagList.size());
//        // 1. 기존 태그 내용이 있다면 전체 삭제
//        if (oldTagList != null){
//            System.out.println("oldTagList != null");
//            tagRepository.deleteAllByReviewId(review); }
//        // 2. 태그 내용이 있다면 태그 수정
//        if (newTagList != null) {
//            for (String newTag : newTagList) {
//                Tag tag = new Tag(newTag, review);
//                review.addSingleTag(tag);
//                tagList.add(tag);
//            }
//            tagRepository.saveAll(tagList);
//        }
//
//
//
//        review.update(reviewRequestDto);
//        reviewRepository.save(review); // 아래의 store.updatePointAvg() 보다 리뷰가 먼저 처리되게 해야한다.
//        storeRepository .save(store.updatePointAvg()); // 별점 평점 구하는 코드
//        reviewImageRepository.saveAll(reviewImageUrlList);
//        System.out.println("=========delete========");
//        reviewImageRepository.deleteAll(reviewImageList);
//    }


    /**
     * 리뷰 삭제
     */
    public void reviewDelete(String socialId, int reviewId) {

        User   user   = userRepository  .findBySocialId(socialId); if (user == null) { throw new CustomException(ErrorCode.NO_USER_FOUND); }   // 유저 유무 확인 예외처리
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.NO_REVIEW_FOUND));                       // 리뷰 유무 확인 예외처리;
        Store store = storeRepository.findById(review.getStoreId().getId()).orElseThrow(()-> new CustomException(ErrorCode.NO_STORE_FOUND));

        List<ReviewImage> imageList =  review.getReviewImageList();

        // todo 1.리뷰삭제 -> 2.이미지 삭제
        reviewRepository.deleteById(review.getId()); // 1
        if (!review.getReviewImageList().isEmpty()) { // 2
            for (ReviewImage reviewImage : imageList) {
                if (reviewImage.getReviewImageUrl() == null || reviewImage.getReviewImageUrl().equals("")) continue;
                System.out.println("delete -> " + reviewImage.getReviewImageUrl().substring(reviewImage.getReviewImageUrl().indexOf("com/") + 4));
                awsS3Manager.deleteFile(reviewImage.getReviewImageUrl().substring(reviewImage.getReviewImageUrl().indexOf("com/") + 4));
            }
        }
        store.removeReview(review);
        storeRepository.save(store.updatePointAvg()); // 별점 평점 구하는 코드
    }




    public List<ReviewMainResponseDto> getRecentReview(BigDecimal lat, BigDecimal lng, int limit) {
        return reviewQueryRepository
                .findRecentReviews(lat, lng, limit)
                .stream().map(review -> new ReviewMainResponseDto(review, review.getStoreId(), review.getUserId())).collect(Collectors.toList());
    }

    public void imageUpload(MultipartFile imageFile, String socialId) {


    }
}
