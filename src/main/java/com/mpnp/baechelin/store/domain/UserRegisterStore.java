package com.mpnp.baechelin.store.domain;

import com.mpnp.baechelin.util.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class UserRegisterStore extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, length = 1)
    private String name;

    @Column(nullable = false, length = 1)
    private String address;

    @Column(nullable = false, length = 1)
    private String elevator;

    @Column(nullable = false, length = 1)
    private String toilet;

    @Column(nullable = false, length = 1)
    private String heightDifferent;

    @Column(nullable = false, length = 1)
    private String approach;

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
