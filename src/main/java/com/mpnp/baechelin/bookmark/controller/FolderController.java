package com.mpnp.baechelin.bookmark.controller;


import com.mpnp.baechelin.bookmark.dto.FolderRequestDto;
import com.mpnp.baechelin.bookmark.dto.FolderResponseDto;
import com.mpnp.baechelin.bookmark.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class FolderController {

    private final FolderService folderService;



    /** 폴더 신규 생성 */
    @PostMapping("/folder")
    public void folder (@RequestBody FolderRequestDto folderRequestDto,
                        @AuthenticationPrincipal User user){

        if(user==null){ throw new IllegalArgumentException("해당하는 회원 정보가 없습니다."); }
        folderService.folder(folderRequestDto, user.getUsername());
    }



    /** 폴더 삭제 -> 삭제 시 안에 담긴 모든 북마크가 삭제됨 */
    @DeleteMapping("/folder/{folderId}")
    public void folderDelete (@PathVariable int folderId) {


        folderService.folderDelete(folderId);
    }



    /** 폴더 명 변경 */
    @PutMapping("/folderUpdate/{folderId}")
    public void folderUpdate (@PathVariable int folderId,
                              @RequestParam String newFolderName){


        folderService.folderUpdate(folderId, newFolderName);
    }



    /** 폴더 리스트 */
    @PostMapping("/folderList")
    public List<FolderResponseDto> folderList (@AuthenticationPrincipal User user){

        return folderService.folderList(user.getUsername());
    }
}
