package com.mpnp.baechelin.bookmark.controller;


import com.mpnp.baechelin.bookmark.dto.FolderReqDTO;
import com.mpnp.baechelin.bookmark.dto.FolderResDTO;
import com.mpnp.baechelin.bookmark.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class FolderController {

    private final FolderService folderService;


    /* 폴더 신규 생성 */
    @PostMapping("/api/folder")
    public void folder (@RequestBody FolderReqDTO folderReqDTO){

        folderService.folder(folderReqDTO);
    }

    /* 폴더 삭제 -> 삭제 시 안에 담긴 모든 북마크가 삭제됨 */

    @DeleteMapping("/api/folder/{folderId}")
    public void folderDelete (@PathVariable int folderId){

        folderService.folderDelete(folderId);
    }

    /* 폴더 명 변경 */

    @PutMapping("/api/folderUpdate/{folderId}")
    public void folderUpdate (@PathVariable int folderId, @RequestParam String newFolderName){
        System.out.println(newFolderName);

        folderService.folderUpdate(folderId, newFolderName);
    }

    @PutMapping("/api/folderList/{userId}")
    public List<FolderResDTO> folderList (@PathVariable int userId){

        return folderService.folderList(userId);
    }
}
