package com.mpnp.baechelin.api.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ApiRequestDto {
    private String key;
    private String type;
    private String service;
    private Integer startIndex;
    private Integer endIndex;
}
