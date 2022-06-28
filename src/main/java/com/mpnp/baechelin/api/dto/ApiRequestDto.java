package com.mpnp.baechelin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ApiRequestDto {
    private String key;
    private String type;
    private String service;
    private Integer start_index;
    private Integer end_index;
}
