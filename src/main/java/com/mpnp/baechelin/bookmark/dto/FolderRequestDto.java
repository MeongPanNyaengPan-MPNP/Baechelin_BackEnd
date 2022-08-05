package com.mpnp.baechelin.bookmark.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Valid
public class FolderRequestDto {
    @Length(max = 15, message = "15자 이하로 입력해 주세요")
    @NotBlank(message = "빈 칸을 입력하지 마세요")
    private String folderName;
}
