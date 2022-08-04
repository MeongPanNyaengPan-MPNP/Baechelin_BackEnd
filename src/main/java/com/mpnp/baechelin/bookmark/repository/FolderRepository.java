package com.mpnp.baechelin.bookmark.repository;

import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Integer> {
    List<Folder> findAllByUserId(User userId);
}
