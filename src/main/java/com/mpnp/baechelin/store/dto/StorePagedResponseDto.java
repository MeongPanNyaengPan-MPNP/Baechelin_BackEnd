package com.mpnp.baechelin.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StorePagedResponseDto {
    private boolean hasNextPage;
    private long totalCount;
    private List<StoreCardResponseDto> cards;

    public StorePagedResponseDto(boolean hasNextPage, List<StoreCardResponseDto> cards, long totalCount) {
        this.hasNextPage = hasNextPage;
        this.cards = cards;
        this.totalCount = totalCount;
    }
}