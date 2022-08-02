package com.mpnp.baechelin.config.batch.requestDto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder @AllArgsConstructor
@ToString
public class StoreDTO {

    private String category;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String SEQ;

    private Long storeId;

    private String GU;

    private String SISULNAME;

    private String ADDR;

    private String TEL;

    private String HOMEPAGE;

    private String HIT;

    private String ST1;

    private String ST2;

    private String ST3;

    private String ST4;

    private String ST5;

    private String ST6;

    private String ST7;

    private String ST8;

    private String ST9;

    private String ST10;

    private String ST11;

    private String ST12;
    public boolean validation(){
        return this.latitude != null && this.longitude != null && this.category != null && this.storeId != null;
    }

}
