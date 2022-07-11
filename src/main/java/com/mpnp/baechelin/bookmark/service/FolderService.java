package com.mpnp.baechelin.bookmark.service;

import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.bookmark.dto.FolderReqDTO;
import com.mpnp.baechelin.bookmark.dto.FolderResDTO;
import com.mpnp.baechelin.bookmark.repository.FolderRepository;
import com.mpnp.baechelin.user.entity.user.User;
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

    @Transactional
    public void folder(FolderReqDTO folderReqDTO) {

        Optional<User> user = userRepository.findById(1);

        Folder folder = Folder.builder()
                .folderName(folderReqDTO.getFolderName())
                .userId(user.get())
                .build();
        folderRepository.save(folder);

    }

    public void folderDelete(int folderId) {
        folderRepository.deleteById(folderId);
    }


    public void folderUpdate(int folderId, String newFolderName) {
        Optional<Folder> folder = folderRepository.findById(folderId);

        folder.get().setFolderName(newFolderName);

        folderRepository.save(folder.get());
    }

    @Transactional(readOnly = true)
    public List<FolderResDTO> folderList(int userId) {
        Optional<User> user = userRepository.findById(userId);

        List<FolderResDTO> folderResDTOList = new ArrayList<>();

        for(Folder obj : user.get().getFolderList()){
            folderResDTOList.add(FolderResDTO.FolderDtoRes(obj));
        }

        return folderResDTOList;
    }
}
