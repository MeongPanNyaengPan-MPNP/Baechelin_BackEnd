package com.mpnp.baechelin.login.jwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter
@NoArgsConstructor
@Entity
public class UserRefreshToken {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    private String refreshToken;

    @Builder
    public UserRefreshToken(String socialId, String refreshToken) {
        this.socialId = socialId;
        this.refreshToken = refreshToken;
    }
}
