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
    private String elevator;
    private String toilet;
    private String parking;
    private String heightDifferent;
    private String approach;
    private int bookmarkId;
    private int storeId;
    private String bookmark;


    public BookmarkInfoDto(Bookmark bookmark){

        this.pointAvg        = Math.round(bookmark.getStoreId().getPointAvg()*10)/10.0;
        this.name            = bookmark.getStoreId().getName();
        this.address         = bookmark.getStoreId().getAddress();
        this.category        = bookmark.getStoreId().getCategory();
        this.phoneNumber     = bookmark.getStoreId().getPhoneNumber();

        this.elevator        = bookmark.getStoreId().getElevator();
        this.toilet          = bookmark.getStoreId().getToilet();
        this.parking         = bookmark.getStoreId().getParking();
        this.heightDifferent = bookmark.getStoreId().getHeightDifferent();
        this.approach        = bookmark.getStoreId().getApproach();

        this.bookmarkId      = bookmark.getId();
        this.storeId         = (int) bookmark.getStoreId().getId();
        this.bookmark = "Y";

        if(!bookmark.getStoreId().getStoreImageList().isEmpty()) {
            this.storeImageList = bookmark.getStoreId().getStoreImageList().get(0).getStoreImageUrl();

        } else if(bookmark.getStoreId().getStoreImageList().isEmpty()) {
            this.storeImageList = "";
        }
    }
}
