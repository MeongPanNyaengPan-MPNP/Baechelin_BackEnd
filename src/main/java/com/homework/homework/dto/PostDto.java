package com.homework.homework.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {
    private String title;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
}
