package com.mpnp.baechelin.bookmark.service;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.bookmark.dto.BookmarkRequestDto;
import com.mpnp.baechelin.bookmark.repository.BookmarkRepository;
import com.mpnp.baechelin.bookmark.repository.FolderRepository;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final FolderRepository folderRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public void bookmark(BookmarkRequestDto bookmarkRequestDto, String socialId) {
        User user = userRepository.findBySocialId(socialId);
        Folder folder = folderRepository.findById(bookmarkRequestDto.getFolderId())
                .orElseThrow(()-> new IllegalArgumentException("폴더가 존재하지 않습니다"));
        Store store = storeRepository.findById(bookmarkRequestDto.getStoreId())
                .orElseThrow(()-> new IllegalArgumentException("가게가 존재하지 않습니다"));

        Bookmark bookmark = Bookmark
                .builder()
                .folderId(folder)
                .storeId(store)
                .userId(user)
                .build();

        storeRepository.save(store.updateBookmarkCount(1));
        bookmarkRepository.save(bookmark);
    }
}
