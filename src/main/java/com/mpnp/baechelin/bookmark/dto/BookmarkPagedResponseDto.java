package com.mpnp.baechelin.bookmark.dto;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class BookmarkPagedResponseDto {
    private boolean hasNextPage;
    private long totalCount;
    private long leftElement;
    private int page;
    private int totalPage;
    private List<BookmarkInfoDto> cards;

    public BookmarkPagedResponseDto(Page<Bookmark> resultStoreList) {
        this.hasNextPage = resultStoreList.hasNext();
        this.totalPage = resultStoreList.getTotalPages() - 1;
        this.cards = resultStoreList.get().collect(Collectors.toList()).stream().map(BookmarkInfoDto::new).collect(Collectors.toList());
        this.totalCount = resultStoreList.getTotalElements();
        this.page = resultStoreList.getNumber();
        this.leftElement = totalCount - (long) page * resultStoreList.getSize() - resultStoreList.getNumberOfElements();
        this.leftElement = this.leftElement < 0 ? 0 : this.leftElement;
    }
}
