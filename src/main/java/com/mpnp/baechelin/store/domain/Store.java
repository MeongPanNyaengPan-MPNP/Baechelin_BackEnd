package com.mpnp.baechelin.store.domain;

<<<<<<< HEAD
=======
import com.mpnp.baechelin.api.dto.ApiResponseDto;
>>>>>>> 670ffb925b6baa9d1f925fef5cdc85b06f4411c8
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
<<<<<<< HEAD
@Builder @AllArgsConstructor
=======
@Builder
@AllArgsConstructor
>>>>>>> 670ffb925b6baa9d1f925fef5cdc85b06f4411c8
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String latitude;

    @Column(nullable = false)
    private String longitude;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String elevator;

    @Column(nullable = false)
    private String toilet;

    @Column(nullable = false)
    private String parking;

    @Column(nullable = false)
<<<<<<< HEAD
=======
    private String phoneNumber;

    @Column(nullable = false)
>>>>>>> 670ffb925b6baa9d1f925fef5cdc85b06f4411c8
    private String heightDifferent;

    @Column(nullable = false)
    private String approach;

    // 연관관계 매핑
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreImage> storeImageList;
<<<<<<< HEAD
=======

    public Store(ApiResponseDto.Row row) {
        this.name = row.getSISULNAME();
        this.address = row.getADDR();
        this.phoneNumber = row.getTEL();
        //접근로
        this.approach = row.getST1();
        //주차장
        this.parking = row.getST2();
        //높이차이제거
        this.heightDifferent = row.getST3();
        //승강기
        this.elevator = row.getST4();
        //화장실
        this.toilet = row.getST5();
    }
>>>>>>> 670ffb925b6baa9d1f925fef5cdc85b06f4411c8
}
