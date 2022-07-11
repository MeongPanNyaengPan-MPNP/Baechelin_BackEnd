package com.mpnp.baechelin.store.domain;

import com.mpnp.baechelin.util.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class UserRegisterStoreImg extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String userRegisterStoreImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERREGISTERSTORE_ID", nullable = false)
    private UserRegisterStore userRegisterStore;

    @Builder
    public UserRegisterStoreImg(String userRegisterStoreImageUrl, UserRegisterStore userRegisterStore) {
        this.userRegisterStoreImageUrl = userRegisterStoreImageUrl;
        this.userRegisterStore = userRegisterStore;
    }

}
