package com.mpnp.baechelin.bookmark.dto;

import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.store.domain.Store;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookmarkDto {

    private List<Store> storeList = new ArrayList<>();

    private List<Folder> folderList = new ArrayList<>();
}
