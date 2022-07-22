package com.mpnp.baechelin.user.dto;


import com.mpnp.baechelin.user.domain.User;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponseDto {
    private String name;
    private String email;

    public UserResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}