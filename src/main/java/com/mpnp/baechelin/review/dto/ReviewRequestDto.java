package com.mpnp.baechelin.review.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {
    //review 테이블 컬럼
    private int                 storeId;      //업장 아이디
    private String              content;      //리뷰 코멘트
    private double              point;        //별점
    private List<String>        tagList;      //태그
    private List<MultipartFile> imageFile;    //리뷰 이미지 사진
}