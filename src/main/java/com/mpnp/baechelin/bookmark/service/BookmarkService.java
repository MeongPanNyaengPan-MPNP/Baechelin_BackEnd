package com.mpnp.baechelin.bookmark.service;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.bookmark.dto.BookmarkInfoDto;
import com.mpnp.baechelin.bookmark.dto.BookmarkRequestDto;
import com.mpnp.baechelin.bookmark.repository.BookmarkRepository;
import com.mpnp.baechelin.bookmark.repository.FolderRepository;
import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreImgRepository;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final FolderRepository   folderRepository;
    private final StoreRepository    storeRepository;
    private final UserRepository     userRepository;
    private final StoreImgRepository storeImgRepository;
//    private final StoreImageRepository storeImageRepository;

    @Transactional
    public void bookmark(BookmarkRequestDto bookmarkRequestDto, String socialId) {
        // 북마크 폴더 생성하는 Flow를 따르므로 bookmarkRequestDto의 folderId는 존재
        Folder folder = folderRepository.findById(bookmarkRequestDto.getFolderId()).orElseThrow(()-> new CustomException(ErrorCode.NO_FOLDER_FOUND));
        Store  store  = storeRepository.findById((long) bookmarkRequestDto.getStoreId()).orElseThrow(()-> new CustomException(ErrorCode.NO_BOOKMARK_FOUND));
        User   user   = userRepository.findBySocialId(socialId); if(user == null) {throw new CustomException(ErrorCode.NO_USER_FOUND); }

        Bookmark bookmark = Bookmark
                .builder()
                .folderId(folder)
                .storeId(store)
                .userId(user)
                .build();

        if (!bookmarkRepository.existsByStoreIdAndUserId(store, user)) {
            bookmarkRepository.save(bookmark);
            storeRepository.save(store.updateBookmarkCount());
        }
    }
    @Transactional
    public void bookmarkDelete(int bookmarkId, String socialId) {
        User user = userRepository.findBySocialId(socialId); if(user == null) { throw new CustomException(ErrorCode.NO_USER_FOUND); }
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElseThrow(() -> new CustomException(ErrorCode.NO_BOOKMARK_FOUND));
        Store store = bookmark.getStoreId();
        store.removeBookmark(bookmark);
        bookmarkRepository.deleteById(bookmarkId);
        storeRepository.save(store.updateBookmarkCount());
    }

    @Transactional
    public List<BookmarkInfoDto> bookmarkTop(String socialId, Pageable pageable) {

        User user = userRepository.findBySocialId(socialId);
        Page<Bookmark> bookmarkPage = bookmarkRepository.findAllByUserId(user, pageable);

        List<BookmarkInfoDto> bookmarkList = new ArrayList<>();
        for(Bookmark bookmark: bookmarkPage){
            BookmarkInfoDto bookmarkInfoDto = new BookmarkInfoDto(bookmark);
            bookmarkList.add(bookmarkInfoDto);
        }
        return bookmarkList;
    }
}
