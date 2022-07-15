package com.mpnp.baechelin.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorePagedResponseDto {
    private boolean hasNextPage;
    private List<StoreCardResponseDto> cards;
}