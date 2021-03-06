package com.mpnp.baechelin.bookmark.service;

import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.bookmark.dto.FolderRequestDto;
import com.mpnp.baechelin.bookmark.dto.FolderResponseDto;
import com.mpnp.baechelin.bookmark.repository.FolderRepository;

import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    /** 폴더 생성 */
    @Transactional
    public void folder(FolderRequestDto folderRequestDto, String socialId) {

        User user = userRepository.findBySocialId(socialId);
        Folder folder = Folder.builder()
                .folderName(folderRequestDto.getFolderName())
                .userId(user)
                .build();
        folderRepository.save(folder);

    }

    /** 폴더 삭제 */
    public void folderDelete(int folderId) {
        folderRepository.deleteById(folderId);
    }

    /** 폴더 수정 */
    public void folderUpdate(int folderId, String newFolderName) {


        Optional<Folder> folder = folderRepository.findById(folderId);
        folder.get().setFolderName(newFolderName);


        folderRepository.save(folder.get());
    }


    /** 폴더 조회 */
    @Transactional(readOnly = true)
    public List<FolderResponseDto> folderList(String socialId) {


        User user = userRepository.findBySocialId(socialId); if(user == null){ throw new IllegalArgumentException("해당하는 유저를 찾을 수 없습니다."); }
        List<FolderResponseDto> folderResponseDtoList = new ArrayList<>();


        for(Folder obj : user.getFolderList()){
            folderResponseDtoList.add(FolderResponseDto.FolderDtoRes(obj));
        }


        return folderResponseDtoList;
    }
}
