package com.mpnp.baechelin.bookmark.controller;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.dto.BookmarkInfoDto;
import com.mpnp.baechelin.bookmark.dto.BookmarkPagedResponseDto;
import com.mpnp.baechelin.bookmark.dto.BookmarkRequestDto;
import com.mpnp.baechelin.bookmark.service.BookmarkService;
import com.mpnp.baechelin.common.SuccessResponse;
import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookmarkController {
    private final BookmarkService bookmarkService;

    /**
     * 북마크 생성 폴더 담기
     */
    @PostMapping("/bookmark")
    public SuccessResponse bookmark(@RequestBody BookmarkRequestDto bookmarkRequestDto,
                                    @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        bookmarkService.bookmark(bookmarkRequestDto, user.getUsername());
        return new SuccessResponse("북마크를 폴더에 저장 완료");
    }

    @DeleteMapping("/bookmark/{bookmarkId}")
    public SuccessResponse bookmarkDelete(@PathVariable int bookmarkId,
                                          @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        bookmarkService.bookmarkDelete(bookmarkId, user.getUsername());
        return new SuccessResponse("북마크를 삭제 완료");
    }

    /**
     * @param folderId 조회하고자 하는 폴더 아이디
     * @param pageable 페이징 객체
     * @param user 접근하는 유저
     * @return 페이징된 객체와 List를 안에 넣어 리턴한다 - bookmark는 항상 "Y"
     */
    @GetMapping("/bookmark/{folderId}")
    public BookmarkPagedResponseDto bookmarkList(@PathVariable int folderId,
                                                 @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                 @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        return bookmarkService.bookmarkList(user.getUsername(), folderId, pageable);
    }

    @GetMapping("/bookmarkTop")
    public ResponseEntity<List<BookmarkInfoDto>> bookmarkTop(@AuthenticationPrincipal User user,
                                                             @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        List<BookmarkInfoDto> bookmarkList = bookmarkService.bookmarkTop(user.getUsername(), pageable);
        return new ResponseEntity<>(bookmarkList, HttpStatus.OK);
    }
}
