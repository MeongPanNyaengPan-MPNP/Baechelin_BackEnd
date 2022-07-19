package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.dto.PublicApiRequestDto;
import com.mpnp.baechelin.api.dto.PublicApiResponseDto;
import com.mpnp.baechelin.api.model.PublicApiForm;
import com.mpnp.baechelin.api.service.PublicApiService;
import com.mpnp.baechelin.api.service.PublicNewApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;

import static java.lang.System.currentTimeMillis;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicApiController {
    private final PublicApiService publicApiService;
    private final PublicNewApiService publicNewApiService;
    @PostMapping("/api")
    public ResponseEntity<String> findPublicApi(@RequestBody PublicApiRequestDto publicApiRequestDto) throws IOException {
        long start = currentTimeMillis();
        ResponseEntity<PublicApiResponseDto> result = ResponseEntity.ok(publicApiService.processApiToDBWithRestTemplate(publicApiRequestDto));
//        ResponseEntity<PublicApiResponseDto> result = ResponseEntity.ok(publicApiService.processApiToDBWithWebclientMono(publicApiRequestDto));
        log.info("Elapsed Time : {}", currentTimeMillis() - start);
        return ResponseEntity.ok("공공 API 저장 완료");
    }
    //TODO 정리하기
    @GetMapping("/new-api")
    public ResponseEntity<PublicApiForm> findNewPublicApi(@RequestParam String serviceKey,
                                                          @RequestParam int rslSize,
                                                          @RequestParam String siDoNm){
        PublicApiForm publicApiForm = publicNewApiService.processNewApi(serviceKey, rslSize, siDoNm);
        return ResponseEntity.ok(publicApiForm);
    }

}
