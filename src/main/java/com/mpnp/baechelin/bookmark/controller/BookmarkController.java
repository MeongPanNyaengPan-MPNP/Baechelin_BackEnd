package com.mpnp.baechelin.bookmark.controller;

import com.mpnp.baechelin.bookmark.dto.BookmarkRequestDto;
import com.mpnp.baechelin.bookmark.service.BookmarkService;
import com.mpnp.baechelin.oauth.entity.UserPrincipal;
import lombok.RequiredArgsConstructor;
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
    public void bookmark(@RequestBody BookmarkRequestDto bookmarkRequestDto, @AuthenticationPrincipal User user){
        if(user==null){
            throw new IllegalArgumentException("로그인 해주세요!");
        }
        bookmarkService.bookmark(bookmarkRequestDto,user.getUsername());
    }
}
