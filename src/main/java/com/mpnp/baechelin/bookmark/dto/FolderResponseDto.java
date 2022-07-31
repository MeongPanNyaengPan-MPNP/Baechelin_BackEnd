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

                BookmarkInfoDto bookmarkInfoDto = new BookmarkInfoDto(bookmark);
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
