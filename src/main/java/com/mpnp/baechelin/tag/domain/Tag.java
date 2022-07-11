package com.mpnp.baechelin.tag.domain;

import com.mpnp.baechelin.review.dto.ReviewReqDTO;
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
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
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

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "REVIEW_ID", nullable = false)
//    private Review reviewId;

    @Builder
    public Tag(ReviewReqDTO reviewReqDTO){
        this.bKiosk      = reviewReqDTO.getBKiosk();            //키오스크 유무
        this.bTable      = reviewReqDTO.getBTable();            //입식 테이블
        this.bWheelchair = reviewReqDTO.getBWheelchair();       //휠체어가 들어갈 수 있는
        this.bMenu       = reviewReqDTO.getBMenu();             //점자 매뉴판
        this.bHelp       = reviewReqDTO.getBHelp();             //직원에 도움 요청
        this.bAutoDoor   = reviewReqDTO.getBAutoDoor();         //자동문
        this.fDelicious  = reviewReqDTO.getFDelicious();        //음식이 맛있는
        this.fClean      = reviewReqDTO.getFClean();            //매장이 깔끔한
        this.fVibe       = reviewReqDTO.getFVibe();             //분위기 좋은
        this.fQuantity   = reviewReqDTO.getFQuantity();         //양이 많은
        this.fGoodToEat  = reviewReqDTO.getFGoodToEat();        //먹기 편한
        this.fPrice      = reviewReqDTO.getFPrice();            //가격이 착한

    }

}
