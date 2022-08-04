package com.mpnp.baechelin.bookmark.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.store.domain.StoreImage;
import lombok.*;

import javax.persistence.Transient;
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
    private String thumbNail;
    private List<BookmarkInfoDto> bookmarkList;

    public FolderResponseDto(Folder folder) {
        this.id = folder.getId();
        this.folderName = folder.getFolderName();
    }

    public static FolderResponseDto FolderDtoRes(Folder folder) {

        /** 북마크의 정보를 담는 작업 */
        List<BookmarkInfoDto> bookmarks = new ArrayList<>();
        if (folder.getBookmarkList() == null || folder.getBookmarkList().isEmpty()) {
            return FolderResponseDto.builder()
                    .folderName(folder.getFolderName())
                    .id(folder.getId())
                    .thumbNail(null)
                    .build();
        }
        for (Bookmark bookmark : folder.getBookmarkList()) {
            BookmarkInfoDto bookmarkInfoDto = new BookmarkInfoDto(bookmark);
            bookmarks.add(bookmarkInfoDto);
        }
        return FolderResponseDto.builder()
                .folderName(folder.getFolderName())
                .id(folder.getId())
                .bookmarkList(bookmarks)
                .thumbNail(bookmarks.size() == 0 ? null : bookmarks.get(bookmarks.size() - 1).getStoreImageList())
                .build();
    }
}
