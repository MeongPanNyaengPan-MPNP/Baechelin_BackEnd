package com.mpnp.baechelin.store.domain;

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

    @OneToMany(mappedBy = "userRegisterStore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRegisterStoreImg> userRegisterStoreImgList;

    @Builder
    public UserRegisterStore(
            String name,
            String address,
            String elevator,
            String toilet,
            String heightDifferent,
            String approach
    ) {
        this.name = name;
        this.address = address;
        this.elevator = elevator;
        this.toilet = toilet;
        this.heightDifferent = heightDifferent;
        this.approach = approach;
    }
}
