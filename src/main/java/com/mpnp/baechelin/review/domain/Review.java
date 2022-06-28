package com.mpnp.baechelin.review.domain;

import com.mpnp.baechelin.util.TimeStamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Review extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //평가(댓글)내용
    @Column(nullable = false)
    private String review;

    //별점
    @Column(nullable = false)
    private int point;

    //리뷰 이미지 URL
    @Column(nullable = false)
    private String reviewImageUrl;

    //배리어프리 키오스크
    @Column(nullable = false)
    private char bKiosk;

    //입식 테이블
    @Column(nullable = false)
    private char bTable;

    //점자 매뉴판
    @Column(nullable = false)
    private char bMenu;

    //휠체어가 들어갈 수 있는
    @Column(nullable = false)
    private char bWheelchair;

    //직원에 도움 요청
    @Column(nullable = false)
    private char bHelp;

    //자동문
    @Column(nullable = false)
    private char bAutoDoor;

    //음식이 맛있는
    @Column(nullable = false)
    private char fDelicious;

    //매장이 깔끔한
    @Column(nullable = false)
    private char fClean;

    //분위기 좋은
    @Column(nullable = false)
    private char fVibe;

    //양이 많은
    @Column(nullable = false)
    private char fQuantity;

    //먹기 편한
    @Column(nullable = false)
    private char fGoodToEat;

    //가격이 착한
    @Column(nullable = false)
    private char fPrice;

//    private int userId;
//    private int storeId;

}
