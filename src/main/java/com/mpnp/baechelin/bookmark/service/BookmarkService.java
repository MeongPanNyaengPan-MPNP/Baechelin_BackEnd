package com.mpnp.baechelin.bookmark.service;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.bookmark.dto.BookmarkReqDTO;
import com.mpnp.baechelin.bookmark.repository.BookmarkRepository;
import com.mpnp.baechelin.bookmark.repository.FolderRepository;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.domain.repository.StoreRepository;
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

        Optional<Folder> folder = folderRepository.findById(bookmarkReqDTO.getFolderId());
        Optional<Store> store = storeRepository.findById(bookmarkReqDTO.getStoreId());

        Bookmark bookmark = Bookmark
                .builder()
                .folderId(folder.get())
                .storeId(store.get())
                .build();

        bookmarkRepository.save(bookmark);
    }
}
