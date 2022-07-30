package com.mpnp.baechelin.bookmark.service;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.bookmark.dto.BookmarkInfoDto;
import com.mpnp.baechelin.bookmark.dto.BookmarkRequestDto;
import com.mpnp.baechelin.bookmark.repository.BookmarkRepository;
import com.mpnp.baechelin.bookmark.repository.FolderRepository;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.domain.StoreImage;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final FolderRepository   folderRepository;
    private final StoreRepository    storeRepository;
    private final UserRepository     userRepository;
    private final StoreImgRepository storeImgRepository;
//    private final StoreImageRepository storeImageRepository;
    public void bookmark(BookmarkRequestDto bookmarkRequestDto, String socialId) {

        Folder folder = folderRepository.findById(bookmarkRequestDto.getFolderId()).orElseThrow(()-> new IllegalArgumentException("폴더가 존재하지 않습니다"));
        Store store   = storeRepository.findById((long) bookmarkRequestDto.getStoreId()).orElseThrow(()-> new IllegalArgumentException("가게가 존재하지 않습니다"));
        User user     = userRepository.findBySocialId(socialId); if(user == null) { throw new IllegalArgumentException("해당하는 유저가 없습니다."); }

        Bookmark bookmark = Bookmark
                .builder()
                .folderId(folder)
                .storeId(store)
                .userId(user)
                .build();
        storeRepository.save(store.updateBookmarkCount());
        bookmarkRepository.save(bookmark);
    }

    public void bookmarkDelete(int bookmarkId, String socialId) {
        User user = userRepository.findBySocialId(socialId); if(user == null) { throw new IllegalArgumentException("해당하는 유저가 없습니다."); }
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElseThrow(() -> new IllegalArgumentException("해당하는 북마크는 이미 삭제 되었습니다"));
        storeRepository.save(bookmark.getStoreId().updateBookmarkCount());
        bookmarkRepository.deleteById(bookmarkId);

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
