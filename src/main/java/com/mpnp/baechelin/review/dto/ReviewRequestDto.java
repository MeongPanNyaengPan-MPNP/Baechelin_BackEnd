package com.mpnp.baechelin.review.dto;

import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {
    //review 테이블 컬럼
    private long storeId;      //업장 아이디
    private String content;      //리뷰 코멘트
    private double point;        //별점
    private List<String> tagList;      //태그
    private List<MultipartFile> imageFile;    //리뷰 이미지 사진

    public List<String> Tags() {

        List<String> tags = new ArrayList<>();

        tags.add("bKiosk");
        tags.add("bTable");
        tags.add("bWheelchair");
        tags.add("bHelp");
        tags.add("bMenu");
        tags.add("bAutoDoor");
        tags.add("fDelicious");
        tags.add("fClean");
        tags.add("fVibe");
        tags.add("fQuantity");
        tags.add("fGoodToEat");
        tags.add("fPrice");

        return tags;
    }

}