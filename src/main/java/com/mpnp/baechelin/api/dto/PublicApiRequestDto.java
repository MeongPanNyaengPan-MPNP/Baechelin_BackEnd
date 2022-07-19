package com.mpnp.baechelin.api.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Valid
public class PublicApiRequestDto {
    @NotBlank(message = "API 키를 입력해주세요")
    private String key;
    @NotBlank(message = "API 리스폰스 타입을 입력해주세요")
    private String type;
    @NotBlank(message = "API 서비스 타입을 입력해주세요")
    private String service;
    @NotNull(message = "API 시작 인덱스를 입력해주세요")
    private Integer startIndex;
    @NotNull(message = "API 마지막 인덱스를 입력해주세요")
    private Integer endIndex;
}
