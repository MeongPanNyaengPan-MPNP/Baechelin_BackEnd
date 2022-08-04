package com.mpnp.baechelin.bookmark.service;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.bookmark.dto.BookmarkInfoDto;
import com.mpnp.baechelin.bookmark.dto.BookmarkPagedResponseDto;
import com.mpnp.baechelin.bookmark.dto.BookmarkRequestDto;
import com.mpnp.baechelin.bookmark.repository.BookmarkRepository;
import com.mpnp.baechelin.bookmark.repository.FolderRepository;
import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreImgRepository;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.store.service.StoreService;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final FolderRepository   folderRepository;
    private final StoreRepository    storeRepository;
    private final UserRepository     userRepository;
    private final StoreService storeService;

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
            storeService.updateBookmarkCnt(store,socialId);
        }
    }
    @Transactional
    public void bookmarkDelete(Long storeId, String socialId) {
        User user = userRepository.findBySocialId(socialId); if(user == null) { throw new CustomException(ErrorCode.NO_USER_FOUND); }
        Store store = storeRepository.findById(storeId).orElseThrow(()-> new CustomException(ErrorCode.NO_STORE_FOUND));
        Bookmark bookmark = bookmarkRepository.findByStoreIdAndUserId(store, user).orElseThrow(() -> new CustomException(ErrorCode.NO_BOOKMARK_FOUND));
        store.removeBookmark(bookmark);
        bookmarkRepository.delete(bookmark);
        storeService.updateBookmarkCnt(store,socialId);
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

    @Transactional
    public BookmarkPagedResponseDto bookmarkList(String socialId, int folderId, Pageable pageable) {
        User user = userRepository.findBySocialId(socialId);
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        Folder folder = folderRepository.findById(folderId).orElseThrow(()-> new CustomException(ErrorCode.NO_FOLDER_FOUND));
        Page<Bookmark> pagedBookmark = bookmarkRepository.findAllByFolderId(folder, pageable);
        return new BookmarkPagedResponseDto(pagedBookmark);
    }
}
