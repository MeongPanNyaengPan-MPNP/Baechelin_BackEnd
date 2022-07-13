package com.mpnp.baechelin.review.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.review.dto.ReviewReqDTO;
import com.mpnp.baechelin.review.repository.ReviewRepository;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.tag.domain.Tag;
import com.mpnp.baechelin.tag.repository.TagRepository;
import com.mpnp.baechelin.user.entity.user.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import com.mpnp.baechelin.util.AwsS3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository  reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final TagRepository     tagRepository;
    private final AwsS3Manager awsS3Manager;

    /** 리뷰작성 */
    public void review(ReviewReqDTO reviewReqDTO) throws IOException {
        Tag tag         = new Tag(reviewReqDTO);        //리뷰 태그
        int storeId     = reviewReqDTO.getStoreId();    //리뷰 업장id
        int userId      = reviewReqDTO.getUserId();     //리뷰 유저id

        tag                     = tagRepository.save(tag);
        Store store   = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당하는 업장이 존재하지 않습니다."));
        User  user    = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));


        String reviewImageUrl = awsS3Manager.uploadFile(reviewReqDTO.getImageFile());
        Review review = new Review(reviewReqDTO, store, tag, user, reviewImageUrl);
        reviewRepository.save(review);
        storeRepository.save(store.updatePointAvg(reviewReqDTO.getPoint()));

//        if(reviewReqDTO.getImageFile() != null) {           //이미지 파일이 있을 경우
//            Review review = new Review(reviewReqDTO, store, tag, user,
//                    upload(reviewReqDTO.getImageFile(), "/Volumes/Data/MY_PROJECT/HangHae99/bae-sulin/src/main/java/com/mpnp/baechelin/fileSample"));
//            reviewRepository.save(review);
//            storeRepository.save(store.updatePointAvg(reviewReqDTO.getPoint())); // Store 별점 업데이트
//        } else if(reviewReqDTO.getImageFile() == null) {    //이미지 파일이 없을 경우
//            Review review = new Review(reviewReqDTO, store, tag, user,"");
//            reviewRepository.save(review);
//            storeRepository.save(store.updatePointAvg(reviewReqDTO.getPoint())); // Store 별점 업데이트
//        }


    }


//    @Value("${cloud.aws.s3.bucket}")
//    public String bucket;  // S3 버킷 이름
//
//    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
//        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
//                .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
//
//        return upload(uploadFile, dirName);
//    }
//
//    // S3로 파일 업로드하기
//    private String upload(File uploadFile, String dirName) {
//        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
//        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
//        removeNewFile(uploadFile);
//        return uploadImageUrl;
//    }
//
//    // S3로 업로드
//    private String putS3(File uploadFile, String fileName) {
//        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
//        return amazonS3Client.getUrl(bucket, fileName).toString();
//    }
//
//    // 로컬에 저장된 이미지 지우기
//    private void removeNewFile(File targetFile) {
//        if (targetFile.delete()) {
//            log.info("File delete success");
//            return;
//        }
//        log.info("File delete fail");
//    }
//
//    // 로컬에 파일 업로드 하기
//    private Optional<File> convert(MultipartFile file) throws IOException {
//        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
//        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
//            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
//                fos.write(file.getBytes());
//            }
//            return Optional.of(convertFile);
//        }
//
//        return Optional.empty();
//    }
}
