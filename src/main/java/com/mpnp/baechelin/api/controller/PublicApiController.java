package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.dto.PublicApiRequestDto;
import com.mpnp.baechelin.api.service.PublicApiService;
import com.mpnp.baechelin.api.service.PublicApiServiceV2;
import com.mpnp.baechelin.common.SuccessResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.io.IOException;

import static java.lang.System.currentTimeMillis;

@RestController
@RequiredArgsConstructor
@Slf4j
@ApiOperation(value="공공 API에 접근해 정보를 받아오는 컨트롤러")
public class PublicApiController {
    private final PublicApiService publicApiService;
    private final PublicApiServiceV2 publicApiServiceV2;
    @ApiOperation(value="공공 API V1을 통해 DB에 정보를 저장/업데이트하는 함수")
    @PostMapping("/api")
    public SuccessResponse getPublicApi(@Valid @RequestBody PublicApiRequestDto publicApiRequestDto) {
        long start = currentTimeMillis();
        publicApiService.processApiV1(publicApiRequestDto);
//        ResponseEntity<PublicApiResponseDto> result = ResponseEntity.ok(publicApiService.processApiToDBWithWebclientMono(publicApiRequestDto));
        log.info("Elapsed Time : {}", currentTimeMillis() - start);
        return new SuccessResponse("공공 API V1 적용 완료");
    }
    //TODO 정리하기
    @ApiOperation(value="공공 API V2을 통해 DB에 정보를 저장/업데이트하는 함수")
    @GetMapping("/new-api")
    public SuccessResponse getPublicApiV2(@RequestParam String siDoNm,
                                          @RequestParam String cggNm){
        publicApiServiceV2.processApi(siDoNm, cggNm, 1);
        return new SuccessResponse("공공 API V2 적용 완료");
    }

    @GetMapping("test")
    public void go() throws IOException, InterruptedException {
        publicApiServiceV2.start();
    }
}
