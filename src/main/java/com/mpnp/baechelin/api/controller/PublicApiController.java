package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.dto.PublicApiRequestDto;
import com.mpnp.baechelin.api.dto.PublicApiResponseDto;
import com.mpnp.baechelin.api.service.PublicApiService;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    //TODO 정리하기
    @GetMapping("/new-api")
    public ResponseEntity<List<StoreCardResponseDto>> findNewPublicApi(@RequestParam String serviceKey,
                                                          @RequestParam int rslSize,
                                                          @RequestParam String siDoNm){
        List<StoreCardResponseDto> result = publicApiService.processNewApi(serviceKey, rslSize, siDoNm);
        return ResponseEntity.ok(result);
    }

}
