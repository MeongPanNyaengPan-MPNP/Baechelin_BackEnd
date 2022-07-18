package com.mpnp.baechelin.bookmark.controller;

import com.mpnp.baechelin.bookmark.dto.BookmarkRequestDto;
import com.mpnp.baechelin.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BookmarkController {
    private final BookmarkService bookmarkService;

    /** 북마크 생성 폴더 담기 */
    @PostMapping("/bookmark")

    public ResponseEntity<?> bookmark(@RequestBody BookmarkRequestDto bookmarkRequestDto,
                                      @AuthenticationPrincipal User user){

        if(user==null){ throw new IllegalArgumentException("해당하는 회원 정보가 없습니다."); }
        bookmarkService.bookmark(bookmarkRequestDto, user.getUsername());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
