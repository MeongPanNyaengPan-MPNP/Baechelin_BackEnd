package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.dto.PublicApiRequestDto;
import com.mpnp.baechelin.api.dto.PublicApiResponseDto;
import com.mpnp.baechelin.api.service.PublicApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;

import static java.lang.System.currentTimeMillis;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicApiController {
    private final PublicApiService publicApiService;

    @PostMapping("/api")
    public ResponseEntity<String> findPublicApi(@RequestBody PublicApiRequestDto publicApiRequestDto) throws IOException {
        long start = currentTimeMillis();
        ResponseEntity<PublicApiResponseDto> result = ResponseEntity.ok(publicApiService.processApiToDBWithRestTemplate(publicApiRequestDto));
//        ResponseEntity<PublicApiResponseDto> result = ResponseEntity.ok(publicApiService.processApiToDBWithWebclientMono(publicApiRequestDto));
        log.info("Elapsed Time : {}", currentTimeMillis() - start);
        return ResponseEntity.ok("공공 API 저장 완료");
    }
}
