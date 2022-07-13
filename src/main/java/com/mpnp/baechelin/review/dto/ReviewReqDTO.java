package com.mpnp.baechelin.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReqDTO {

    //review 테이블 컬럼
    private int           userId;       //유저 아이디
    private int           storeId;      //업장 아이디
    private String        comment;      //리뷰 코멘트
    private double        point;        //별점
    private MultipartFile imageFile;    //리뷰 이미지 사진

    // tag 테이블 컬럼
    @Builder.Default
    private char bKiosk      = 'N';     //키오스크 유무
    @Builder.Default
    private char bTable      = 'N';     //입식 테이블
    @Builder.Default
    private char bMenu       = 'N';     //점자 매뉴판
    @Builder.Default
    private char bWheelchair = 'N';     //휠체어가 들어갈 수 있는
    @Builder.Default
    private char bHelp       = 'N';     //직원에 도움 요청
    @Builder.Default
    private char bAutoDoor   = 'N';     //자동문
    @Builder.Default
    private char fDelicious  = 'N';     //음식이 맛있는
    @Builder.Default
    private char fClean      = 'N';     //매장이 깔끔한
    @Builder.Default
    private char fVibe       = 'N';     //분위기 좋은
    @Builder.Default
    private char fQuantity   = 'N';     //양이 많은
    @Builder.Default
    private char fGoodToEat  = 'N';     //먹기 편한
    @Builder.Default
    private char fPrice      = 'N';     //가격이 착한

}