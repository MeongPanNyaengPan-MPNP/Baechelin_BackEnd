package com.homework.homework.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class CommentDto {
    private String nickname;

    @NotEmpty(message = "댓글 내용을 작성해주세요.")
    private String comment;
}
