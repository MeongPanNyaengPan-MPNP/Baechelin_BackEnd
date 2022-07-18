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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final AwsS3Manager          awsS3Manager;
    private final TagRepository         tagRepository;
    private final UserRepository        userRepository;
    private final StoreRepository       storeRepository;
    private final ReviewRepository      reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewQueryRepository reviewQueryRepository;



    /** 리뷰 작성 */
    public void review(ReviewRequestDto reviewRequestDto, String socialId) throws IOException {

        int    storeId = reviewRequestDto.getStoreId();
        Store  store   = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));
        User   user    = userRepository.findBySocialId(socialId);
        Review review  = new Review(reviewRequestDto, store, user);


        // todo 태크 매핑
        List<Tag> tagList = new ArrayList<>();
        for (String s : reviewRequestDto.getTagList()) {
            System.out.println("tag --> "+ s);
            tagList.add(new Tag(s, review));
        } // 태그 -> 엔티티 변환


        List<ReviewImage> reviewImageUrlList = new ArrayList<>();
        List<MultipartFile>   newReviewImage = reviewRequestDto.getImageFile();


        // todo 이미지가 널값이 아니라면 업로드 실행
        if(newReviewImage != null) {
            for (MultipartFile reviewImageFile : newReviewImage) {
                String fileDir = awsS3Manager.uploadFile(reviewImageFile);
                log.info("upload --> "+ fileDir);
                reviewImageUrlList.add(ReviewImage.builder().reviewId(review).reviewImageUrl(fileDir).build());
            } // 리뷰이미지 -> url -> 엔티티 변환
        }


        tagRepository        .saveAll(tagList);
        reviewImageRepository.saveAll(reviewImageUrlList);
        reviewRepository     .save(review); // 아래의 {store.updatePointAvg()} 보다 리뷰가 먼저 처리되게 해야한다.
        storeRepository.save(store.updatePointAvg(reviewRequestDto.getPoint())); //별점 평균 구하는 코드
    }



    public List<ReviewResponseDto> getReview(int storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다"));
        return reviewRepository.findAllByStoreId(store)
                .stream().map(ReviewResponseDto::new).collect(Collectors.toList());
    }




    /** 리뷰 수정 */
    public void reviewUpdate(ReviewRequestDto reviewRequestDto, String socialId, int reviewId) throws IOException {

        int       storeId    = reviewRequestDto.getStoreId();
        User      user       = userRepository.findBySocialId(socialId); if(user == null){ new IllegalArgumentException("해당하는 소셜아이디를 찾을 수 없습니다."); }
        Store     store      = storeRepository.findById(storeId)       .orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));
        Review    review     = reviewRepository.findById(reviewId)     .orElseThrow(() -> new IllegalArgumentException("해당하는 리뷰가 없습니다."));


        List<MultipartFile> newImageFileList = reviewRequestDto.getImageFile(); // 새로운 이미지 파일
        List<ReviewImage>   oldImageFileList = review.getReviewImageList();     // 기존에 이미지 파일
        List<ReviewImage> reviewImageUrlList = new ArrayList<>();               // 이미지 파일을 담을 리스트


        // todo 이미지 삭제 후 수정 작업 (1 -> 2)
        // 1.기존리뷰에 기존 이미지가 있다면 삭제
        if(oldImageFileList != null) {
            for (ReviewImage reviewImage : oldImageFileList) {
                System.out.println("check -> "  + reviewImage.getReviewImageUrl());
                System.out.println("delete -> " + reviewImage.getReviewImageUrl().substring(reviewImage.getReviewImageUrl().indexOf("com/") + 4));
                awsS3Manager.deleteFile(reviewImage.getReviewImageUrl().substring(reviewImage.getReviewImageUrl().indexOf("com/") + 4));

            }
            reviewImageRepository.deleteByReviewId(review);
        }

        // 2.수정할 이미지가 있다면 업로드
        if(newImageFileList != null) {
            System.out.println("newImageFileList != null");
            for (MultipartFile reviewImageFile : newImageFileList) {
                String fileDir = awsS3Manager.uploadFile(reviewImageFile);
                System.out.println("update --> " + fileDir);
                reviewImageUrlList.add(ReviewImage.builder().reviewId(review).reviewImageUrl(fileDir).build());
            } // 리뷰이미지 -> url -> 엔티티 변환
        }

        List<Tag>       tagList = new ArrayList<>();               // 태그를 담는 리스트
        List<String> newTagList = reviewRequestDto.getTagList();   // 새로운 태그


        // todo 태그 수정 작업
        List<Tag> oldTagList = tagRepository.findAllByReviewId(review);
        System.out.println("log Tag SIZE --> "+ oldTagList.size());
        // 1. 기존 태그 내용이 있다면 전체 삭제
        if (oldTagList != null){
            System.out.println("oldTagList != null");
            tagRepository.deleteAByReviewId(review); }
        // 2. 태그 내용이 있다면 태그 수정
        if (newTagList != null) {
            for (String newTag : newTagList) {
                Tag tag = new Tag(newTag, review);
                review.addSingleTag(tag);
                tagList.add(tag);
            }
            tagRepository.saveAll(tagList);
        }


        review.update(reviewRequestDto);
        reviewRepository.save(review); // 아래의 store.updatePointAvg() 보다 리뷰가 먼저 처리되게 해야한다.
        storeRepository .save(store.updatePointAvg(reviewRequestDto.getPoint())); // 별점 평점 구하는 코드
        reviewImageRepository.saveAll(reviewImageUrlList);
    }

    /** 리뷰 삭제 */
    public void reviewDelete(String socialId, int reviewId) {

        User              user      = userRepository.findBySocialId(socialId);  // 유저 매핑
        Optional<Review>  review    = reviewRepository.findById(reviewId);      // 리뷰 매핑


        if(user == null){ new IllegalArgumentException("해당하는 소셜아이디를 찾을 수 없습니다."); }      // 유저 유무 확인 예외처리
        review.orElseThrow(() -> new IllegalArgumentException("해당하는 리뷰가 이미 삭제 되었습니다.")); // 리뷰 유무 확인 예외처리


        List<ReviewImage> imageList = review.get().getReviewImageList();


        // todo 1.리뷰삭제 -> 2.이미지 삭제
        reviewRepository.deleteById(review.get().getId()); // 1
        if(!review.get().getReviewImageList().isEmpty()) { // 2
            for (ReviewImage reviewImage : imageList) {
                System.out.println("delete -> "+reviewImage.getReviewImageUrl().substring(reviewImage.getReviewImageUrl().indexOf("com/")+4));
                awsS3Manager.deleteFile(reviewImage.getReviewImageUrl().substring(reviewImage.getReviewImageUrl().indexOf("com/")+4));
            }
        }
    }

    public List<ReviewMainResponseDto> getRecentReview(BigDecimal lat, BigDecimal lng, int limit) {
        return reviewQueryRepository
                .findRecentReviews(lat, lng, limit)
                .stream().map(review -> new ReviewMainResponseDto(review, review.getStoreId(), review.getUserId())).collect(Collectors.toList());
    }

}
