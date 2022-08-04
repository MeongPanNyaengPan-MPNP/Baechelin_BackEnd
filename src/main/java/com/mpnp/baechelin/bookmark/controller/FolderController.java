package com.mpnp.baechelin.bookmark.controller;


import com.mpnp.baechelin.bookmark.dto.BookmarkPagedResponseDto;
import com.mpnp.baechelin.bookmark.dto.FolderRequestDto;
import com.mpnp.baechelin.bookmark.dto.FolderResponseDto;
import com.mpnp.baechelin.bookmark.service.BookmarkService;
import com.mpnp.baechelin.bookmark.service.FolderService;
import com.mpnp.baechelin.common.SuccessResponse;
import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
public class FolderController {

    private final FolderService folderService;
    private final BookmarkService bookmarkService;

    /** 폴더 신규 생성 */
    @PostMapping("/folder")
    public SuccessResponse folder (@RequestBody FolderRequestDto folderRequestDto,
                                   @AuthenticationPrincipal User user){
        if(user==null){
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        folderService.folder(folderRequestDto, user.getUsername());
        return new SuccessResponse("폴더 생성 완료");
    }

    /** 폴더 삭제 -> 삭제 시 안에 담긴 모든 북마크가 삭제됨 */
    @DeleteMapping("/folder/{folderId}")
    public SuccessResponse folderDelete (@PathVariable int folderId,
                                           @AuthenticationPrincipal User user) {

        if(user==null){ throw new CustomException(ErrorCode.NO_USER_FOUND); }
        folderService.folderDelete(folderId);
        return new SuccessResponse("폴더 및 북마크 삭제 완료");
    }

    /** 폴더 명 변경 */
    @PutMapping("/folderUpdate/{folderId}")
    public SuccessResponse folderUpdate (@PathVariable int folderId,
                                           @RequestParam String newFolderName,
                                           @AuthenticationPrincipal User user){
        if(user==null){ throw new CustomException(ErrorCode.NO_USER_FOUND); }
        folderService.folderUpdate(folderId, newFolderName);
        return new SuccessResponse("폴더명 변경 완료");
    }

    /** 폴더 리스트 */
    @GetMapping("/folderList")
    public List<FolderResponseDto> folderList (@AuthenticationPrincipal User user){
//        return folderService.folderList(user.getUsername());
        return folderService.folderList(user.getUsername());
    }

    /**
     * @param folderId 조회하고자 하는 폴더 아이디
     * @param pageable 페이징 객체
     * @param user 접근하는 유저
     * @return 페이징된 객체와 List를 안에 넣어 리턴한다 - bookmark는 항상 "Y"
     */
    @GetMapping("/folder/{folderId}")
    public BookmarkPagedResponseDto bookmarkList(@PathVariable int folderId,
                                                 @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                 @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        return bookmarkService.bookmarkList(user.getUsername(), folderId, pageable);
    }
}
