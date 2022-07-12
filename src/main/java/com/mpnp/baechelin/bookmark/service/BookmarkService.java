package com.mpnp.baechelin.bookmark.service;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.bookmark.dto.BookmarkReqDTO;
import com.mpnp.baechelin.bookmark.repository.BookmarkRepository;
import com.mpnp.baechelin.bookmark.repository.FolderRepository;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final FolderRepository folderRepository;
    private final StoreRepository storeRepository;

    public void bookmark(BookmarkReqDTO bookmarkReqDTO) {

        Folder folder = folderRepository.findById(bookmarkReqDTO.getFolderId())
                .orElseThrow(()-> new IllegalArgumentException("폴더가 존재하지 않습니다"));
        Store store = storeRepository.findById(bookmarkReqDTO.getStoreId())
                .orElseThrow(()-> new IllegalArgumentException("가게가 존재하지 않습니다"));

        Bookmark bookmark = Bookmark
                .builder()
                .folderId(folder)
                .storeId(store)
                .build();

        storeRepository.save(store.updateBookmarkCount(1));
        bookmarkRepository.save(bookmark);
    }
}
