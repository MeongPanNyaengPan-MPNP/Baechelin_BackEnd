package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.dto.ApiRequestDto;
import com.mpnp.baechelin.api.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;

    @GetMapping("/api")
//    public ApiResponseDto findPublicApi(@ModelAttribute ApiRequestDto apiRequestDto) throws  IOException {
    public String findPublicApi(@RequestBody ApiRequestDto apiRequestDto) throws IOException {
        return apiService.processApiToDBWithWebclient(apiRequestDto);
    }
}
