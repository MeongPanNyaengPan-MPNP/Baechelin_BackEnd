package com.mpnp.baechelin.review.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReqDTO {

    //review 테이블 컬럼

    private int           reviewId;
    private String        socialId;     //소셜 아이디
    private int           storeId;      //업장 아이디
    private String        comment;      //리뷰 코멘트
    private double        point;        //별점
    private MultipartFile imageFile;    //리뷰 이미지 사진

    // tag 테이블 컬럼
    private char bKiosk      = 'N';     //키오스크 유무
    private char bTable      = 'N';     //입식 테이블
    private char bMenu       = 'N';     //점자 매뉴판
    private char bWheelchair = 'N';     //휠체어가 들어갈 수 있는
    private char bHelp       = 'N';     //직원에 도움 요청
    private char bAutoDoor   = 'N';     //자동문
    private char fDelicious  = 'N';     //음식이 맛있는
    private char fClean      = 'N';     //매장이 깔끔한
    private char fVibe       = 'N';     //분위기 좋은
    private char fQuantity   = 'N';     //양이 많은
    private char fGoodToEat  = 'N';     //먹기 편한
    private char fPrice      = 'N';     //가격이 착한


    public ReviewReqDTO(String socialId, int storeId, String comment, double point, MultipartFile imageFile) {
        this.socialId = socialId;
        this.storeId = storeId;
        this.comment = comment;
        this.point = point;
        this.imageFile = imageFile;
    }

}