package com.mpnp.baechelin.bookmark.repository;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends CrudRepository<Bookmark, Integer> {
    Page<Bookmark> findAllByFolderId(Folder folderId, Pageable pageable);
    boolean existsByStoreIdAndUserId(Store store, User user);

    Optional<Bookmark> findByStoreIdAndUserId(Store store, User user);

    @Query("select b.storeId from Bookmark b where b.folderId=:folderId order by b.createdAt desc")
    List<Integer> findLatestFolder(@Param("folderId") int folderId);

    Page<Bookmark> findAllByUserId(User user, Pageable pageable);
}