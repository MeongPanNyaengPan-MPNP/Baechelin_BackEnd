package com.mpnp.baechelin.bookmark.dto;


import com.mpnp.baechelin.store.domain.StoreImage;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkInfoDto {

    private double pointAvg;
    private String name;
    private String address;
    private String category;
    private String phoneNumber;
    private int bookmarkId;
    private int storeId;
    String storeImageList;
}
