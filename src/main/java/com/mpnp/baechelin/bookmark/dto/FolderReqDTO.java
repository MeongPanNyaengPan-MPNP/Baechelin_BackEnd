package com.mpnp.baechelin.bookmark.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderReqDTO {

    private String folderName = "미분류";
}
