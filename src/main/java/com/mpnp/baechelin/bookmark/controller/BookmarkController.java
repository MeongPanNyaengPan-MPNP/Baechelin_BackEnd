package com.mpnp.baechelin.bookmark.controller;

import com.mpnp.baechelin.bookmark.dto.BookmarkReqDTO;
import com.mpnp.baechelin.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BookmarkController {
    private final BookmarkService bookmarkService;

    /* 북마크 생성 폴더 담기 */
    @PostMapping("/api/bookmark")
    public void bookmark(@RequestBody BookmarkReqDTO bookmarkReqDTO){

        bookmarkService.bookmark(bookmarkReqDTO);
    }
}
