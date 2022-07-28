package com.mpnp.baechelin.review.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoResponseDto {

    boolean hasNextPage;            // 이전페이지가 없다면 true
    boolean hasPreviousPage;        // 다음페이지가 없다면 true

    private int size;           // 페이지안에 있는 데이터 갯수
    private int totalPages;     // 총 페이지 수
    private int totalElements;  // 총 데이터 갯수
    private int number;         // 현재 페이지 넘버

    List<ReviewResponseDto> reviewResponseDtoList;

}
