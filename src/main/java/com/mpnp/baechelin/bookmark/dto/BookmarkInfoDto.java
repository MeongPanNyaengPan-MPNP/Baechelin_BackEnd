package com.mpnp.baechelin.bookmark.dto;


import com.mpnp.baechelin.bookmark.domain.Bookmark;
import lombok.*;


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
    private String storeImageList;
    private int bookmarkId;
    private int storeId;


    public BookmarkInfoDto(Bookmark bookmark){

        this.pointAvg       = Math.round(bookmark.getStoreId().getPointAvg()*10)/10.0;
        this.name           = bookmark.getStoreId().getName();
        this.address        = bookmark.getStoreId().getAddress();
        this.category       = bookmark.getStoreId().getCategory();
        this.phoneNumber    = bookmark.getStoreId().getPhoneNumber();
        this.bookmarkId     = bookmark.getId();
        this.storeId        = (int) bookmark.getStoreId().getId();

        if(bookmark.getStoreId().getStoreImageList() == null) {
            this.storeImageList = bookmark.getStoreId().getStoreImageList().get(0).getStoreImageUrl();

        } else if(bookmark.getStoreId().getStoreImageList() != null) {
            this.storeImageList = "";
        }
    }
}
