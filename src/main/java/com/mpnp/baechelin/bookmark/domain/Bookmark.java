package com.mpnp.baechelin.bookmark.domain;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.util.TimeStamped;
import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="FOLDER_ID", nullable = false)
    private Folder folderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="STORE_ID", nullable = false)
    private Store storeId;

}
