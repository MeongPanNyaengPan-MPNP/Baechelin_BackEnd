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

    private List<BookmarkInfoDto> bookmarkList;

    public FolderResponseDto(Folder folder) {
        this.id = folder.getId();
        this.folderName = folder.getFolderName();
    }

    public static FolderResponseDto FolderDtoRes(Folder folder) {

        /** 북마크의 정보를 담는 작업 */
        List<BookmarkInfoDto> bookmarks = new ArrayList<>();

        if(folder.getBookmarkList() != null) {
            for (Bookmark bookmark : folder.getBookmarkList()) {

                double pointAvg     = Math.round(bookmark.getStoreId().getPointAvg()*10)/10.0;  // 업장 별점
                String name         = bookmark.getStoreId().getName();                          // 업장 이름
                String address      = bookmark.getStoreId().getAddress();                       // 업장 주소
                String category     = bookmark.getStoreId().getCategory();                      // 업장 카테고리
                String PhoneNumber  = bookmark.getStoreId().getPhoneNumber();                   // 업장 전화번호
                int bookmarkId      = bookmark.getId();
                int storeId         = (int) bookmark.getStoreId().getId();

                List<BookmarkInfoDto> BookmarkInfoDtoList = new ArrayList<>();            // 정보를 담는 리스트
                List<StoreImage> storeImageList = bookmark.getStoreId().getStoreImageList();// 업장 이미지 리스트

                BookmarkInfoDto bookmarkInfoDto = BookmarkInfoDto
                        .builder()
                        .bookmarkId(bookmarkId)
                        .storeId(storeId)
                        .address(address)
                        .phoneNumber(PhoneNumber)
                        .category(category)
                        .pointAvg(pointAvg)
                        .name(name)
                        .storeImageList(!storeImageList.isEmpty() ? storeImageList.get(0).getStoreImageUrl():"")
                        .build();

                bookmarks.add(bookmarkInfoDto);

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
