package com.mpnp.baechelin.bookmark.dto;


import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FolderReqDTO {

    private String folderName = "미분류";
    private String socialId;
}
