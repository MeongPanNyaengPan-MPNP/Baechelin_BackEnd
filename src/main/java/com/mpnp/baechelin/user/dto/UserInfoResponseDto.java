package com.mpnp.baechelin.user.dto;


import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoResponseDto {
    private String name;
    private String email;

    public UserInfoResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}