package com.mpnp.baechelin.bookmark.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.util.TimeStamped;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folder extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID", nullable = false)
    private User userId;

    @Column(nullable = false)
    private String folderName;

    @JsonIgnore
    @OneToMany(mappedBy = "folderId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();
}
