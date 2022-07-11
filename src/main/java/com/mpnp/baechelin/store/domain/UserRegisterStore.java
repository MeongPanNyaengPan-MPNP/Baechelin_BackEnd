package com.mpnp.baechelin.store.domain;

import com.mpnp.baechelin.user.entity.user.User;
import com.mpnp.baechelin.util.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class UserRegisterStore extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 1)
    private String elevator;

    @Column(nullable = false, length = 1)
    private String toilet;

    @Column(nullable = false, length = 1)
    private String heightDifferent;

    @Column(nullable = false, length = 1)
    private String approach;

    // 연관관계 매핑
    @OneToMany(mappedBy = "userRegisterStore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRegisterStoreImg> userRegisterStoreImgList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Builder
    public UserRegisterStore(
            String name,
            String address,
            String elevator,
            String toilet,
            String heightDifferent,
            String approach,
            User user
    ) {
        this.name = name;
        this.address = address;
        this.elevator = elevator;
        this.toilet = toilet;
        this.heightDifferent = heightDifferent;
        this.approach = approach;
        this.user = user;
    }
}
