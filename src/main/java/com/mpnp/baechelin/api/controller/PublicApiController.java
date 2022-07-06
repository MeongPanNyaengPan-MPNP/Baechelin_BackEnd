package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.dto.PublicApiRequestDto;
import com.mpnp.baechelin.api.dto.PublicApiResponseDto;
import com.mpnp.baechelin.api.service.PublicApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PublicApiController {
    private final PublicApiService publicApiService;

    @GetMapping("/api")
    public ResponseEntity<?> findPublicApi(@RequestBody PublicApiRequestDto publicApiRequestDto) throws IOException {
        return ResponseEntity.ok(publicApiService.processApiToDBWithWebclientMono(publicApiRequestDto));
    }

    /**
     * API 결합하기
     * FLOW : 공공 API -> 카카오맵 API(위도, 경도 변환) 1차 -> 카카오맵 API(카테고리 변환) 2차 -> 리턴
     * @return
     */
    @GetMapping("/api/combine")
    public Flux<PublicApiResponseDto> combineApi(){
        //공공 API
        return null;
        //
    }
}
