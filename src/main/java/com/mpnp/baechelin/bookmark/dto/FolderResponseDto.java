package com.mpnp.baechelin.bookmark.dto;


import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.store.domain.StoreImage;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderResponseDto {
    private int id;
    private String folderName;
    private List<List<String>> bookmarkList;

    public FolderResponseDto(Folder folder) {
        this.id = folder.getId();
        this.folderName = folder.getFolderName();
    }

    public static FolderResponseDto FolderDtoRes(Folder folder) {

        /** 북마크의 정보를 담는 작업 */
        List<List<String>> bookmarks = new ArrayList<>();

        if(folder.getBookmarkList() != null) {
            for (Bookmark bookmark : folder.getBookmarkList()) {

                String pointAvg     = String.valueOf(bookmark.getStoreId().getPointAvg());  // 업장 별점
                String name         = bookmark.getStoreId().getName();                      // 업장 이름
                String address      = bookmark.getStoreId().getAddress();                   // 업장 주소
                String category     = bookmark.getStoreId().getCategory();                  // 업장 카테고리
                String PhoneNumber  = bookmark.getStoreId().getPhoneNumber();               // 업장 전화번호

                List<StoreImage> storeImageList = bookmark.getStoreId().getStoreImageList();// 업장 이미지 리스트
                List<String> tempBookmarkList   = new ArrayList<>();                        // 정보를 담는 리스트

                tempBookmarkList.add(name);
                tempBookmarkList.add(pointAvg);
                tempBookmarkList.add(address);
                tempBookmarkList.add(category);
                tempBookmarkList.add(PhoneNumber);
                tempBookmarkList.add(storeImageList.get(0).getStoreImageUrl());

                bookmarks.add(tempBookmarkList);

            }
        } else if(folder.getBookmarkList() == null) {

            return FolderResponseDto.builder()
                    .folderName(folder.getFolderName())
                    .id(folder.getId())
                    .build();

        }

        return FolderResponseDto.builder()
                .folderName(folder.getFolderName())
                .id(folder.getId())
                .bookmarkList(bookmarks)
                .build();
    }
}
