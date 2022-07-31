package com.mpnp.baechelin.store.dto;

import com.mpnp.baechelin.store.domain.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StorePagedResponseDto {
    private boolean hasNextPage;
    private long totalCount;
    private long leftElement;
    private int page;
    private int totalPage;
    private List<StoreCardResponseDto> cards;

    public StorePagedResponseDto(Page<Store> resultStoreList, List<StoreCardResponseDto> cards) {
        this.hasNextPage = resultStoreList.hasNext();
        this.totalPage = resultStoreList.getTotalPages();
        this.cards = cards;
        this.totalCount = resultStoreList.getTotalElements();
        this.page = resultStoreList.getNumber();
        this.leftElement = totalCount - (long) page * resultStoreList.getSize() - resultStoreList.getNumberOfElements();
    }
}