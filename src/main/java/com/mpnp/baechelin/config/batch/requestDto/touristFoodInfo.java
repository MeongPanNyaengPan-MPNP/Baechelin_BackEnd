package com.mpnp.baechelin.config.batch.requestDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class touristFoodInfo {

    String list_total_count;
    Result RESULT;
    List<StoreDTO> row;
}
