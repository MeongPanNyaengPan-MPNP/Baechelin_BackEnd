package com.mpnp.baechelin.bookmark.dto;


import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderResDTO {
    private int id;
    private String folderName;
    private List<List<String>> bookmarkList;

    public FolderResDTO(Folder folder) {
        this.id = folder.getId();
        this.folderName = folder.getFolderName();
    }

    public static FolderResDTO FolderDtoRes(Folder folder) {
        /** 북마크의 정보를 담는 작업 */
        List<List<String>> bookmarks = new ArrayList<>();
        if(folder.getBookmarkList() != null) {
            for (Bookmark bookmark : folder.getBookmarkList()) {
                List<String> tempBookmarkList = new ArrayList<>();
                tempBookmarkList.add(bookmark.getStoreId().getName());
                bookmarks.add(tempBookmarkList);
            }
        } else if(folder.getBookmarkList() == null) {
            return FolderResDTO.builder()
                    .folderName(folder.getFolderName())
                    .id(folder.getId())
                    .build();
        }

        return FolderResDTO.builder()
                .folderName(folder.getFolderName())
                .id(folder.getId())
                .bookmarkList(bookmarks)
                .build();
    }
}
