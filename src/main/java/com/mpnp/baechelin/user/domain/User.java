package com.mpnp.baechelin.user.domain;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.util.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, unique = true)
    private int kakaoId;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String profileImageUrl;

    @Builder
    public User(int kakaoId, String email, String name, String profileImageUrl) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> BookmarkList;

}
