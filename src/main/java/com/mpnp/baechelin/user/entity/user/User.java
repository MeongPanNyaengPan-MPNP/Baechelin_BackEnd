package com.mpnp.baechelin.user.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.oauth.entity.ProviderType;
import com.mpnp.baechelin.oauth.entity.RoleType;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.util.TimeStamped;
import io.micrometer.core.annotation.Counted;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class User extends TimeStamped {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, unique = true)
    private String socialId;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 1)
    private String emailVerifiedYn; // 이메일을 필수값으로 설정해줬기 때문에 없어도 될 것 같다.

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    // 연관관계 매핑
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> folderList;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsList;

    @Builder
    public User(String socialId,
                String name,
                String email,
                String emailVerifiedYn,
                String profileImageUrl,
                ProviderType providerType,
                RoleType roleType) {
        this.socialId = socialId;
        this.name = name;
        this.password = "NO_PASS";
        this.email = email != null ? email : "NO_EMAIL";
        this.emailVerifiedYn = emailVerifiedYn;
        this.profileImageUrl = profileImageUrl != null ? profileImageUrl : "";
        this.providerType = providerType;
        this.roleType = roleType;
    }

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> folderList = new ArrayList<>();
}
