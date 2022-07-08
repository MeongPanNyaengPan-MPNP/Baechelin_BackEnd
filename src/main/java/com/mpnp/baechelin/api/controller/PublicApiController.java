package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.dto.PublicApiRequestDto;
import com.mpnp.baechelin.api.dto.PublicApiResponseDto;
import com.mpnp.baechelin.api.service.PublicApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PublicApiController {
    private final PublicApiService publicApiService;

    @PostMapping("/api")
    public ResponseEntity<?> findPublicApi(@RequestBody PublicApiRequestDto publicApiRequestDto) throws IOException {
        return ResponseEntity.ok(publicApiService.processApiToDBWithRestTemplate(publicApiRequestDto));
//        return ResponseEntity.ok(publicApiService.processApiToDBWithWebclientMono(publicApiRequestDto));
    }


}
