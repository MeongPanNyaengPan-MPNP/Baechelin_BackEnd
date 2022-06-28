package com.mpnp.baechelin.api.service;

import com.mpnp.baechelin.api.dto.ApiResponseDto;

public class Stores {

    public Stores(ApiResponseDto.Row row) {
        this. = row.getSEQ();
        this. = row.getGU();
        this. = row.getADDR();
        this. = row.getTEL();
        this. = row.getHOMEPAGE();
        this. = row.getHIT();

        //접근로
        this. = row.getST1();

        //주차장
        this. = row.getST2();

        //높이차이제거
        this. = row.getST3();

        //승강기
        this. = row.getST4();

        //화장실
        this. = row.getST5();
        this. = row.getST6();
        this. = row.getST7();
        this. = row.getST8();
        this. = row.getST9();
        this. = row.getST10();
        this. = row.getST11();
        this. = row.getST12();


    }
}
