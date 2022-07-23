package com.mpnp.baechelin.store.controller;

import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.login.jwt.AuthToken;
import com.mpnp.baechelin.login.jwt.AuthTokenProvider;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.mpnp.baechelin.store.dto.StorePagedResponseDto;
import com.mpnp.baechelin.store.service.StoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Api(tags = {"매장 리스트를 반환하는 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    private final AuthTokenProvider tokenProvider;

    @ApiOperation(value = "조건에 맞는 업장 목록을 반환하는 메소드")
    @GetMapping("/near")
//    public List<StoreCardResponseDto> getStoreInRange(@RequestParam(required = false) BigDecimal latStart,
    public StorePagedResponseDto getStoreInRange(@RequestParam(required = false) BigDecimal latStart,
                                                 @RequestParam(required = false) BigDecimal latEnd,
                                                 @RequestParam(required = false) BigDecimal lngStart,
                                                 @RequestParam(required = false) BigDecimal lngEnd,
                                                 @RequestParam(required = false) BigDecimal lat,
                                                 @RequestParam(required = false) BigDecimal lng,
                                                 @RequestParam(required = false) String category,
                                                 @RequestParam(required = false) List<String> facility,
                                                 @PageableDefault Pageable pageable,
                                                 @AuthenticationPrincipal User user) {
        if (lat != null && lng != null)
            return storeService.getStoreInRangeMain(lat, lng, category, facility, pageable, user == null ? null : user.getUsername());
        else
            return storeService.getStoreInRange(latStart, latEnd, lngStart, lngEnd, category, facility, pageable, user == null ? null : user.getUsername());
    }

    @GetMapping("/point")
    public StorePagedResponseDto getStoreInRangeHighPoint(@RequestParam(required = false) BigDecimal lat,
//    public List<StoreCardResponseDto> getStoreInRangeHighPoint(@RequestParam(required = false) BigDecimal lat,
                                                          @RequestParam(required = false) BigDecimal lng,
                                                          @RequestParam(required = false) String category,
                                                          @RequestParam(required = false) List<String> facility,
                                                          @PageableDefault Pageable pageable,
                                                          @AuthenticationPrincipal User user) {
        return storeService.getStoreInRangeHighPoint(lat, lng, category, facility, pageable, user == null ? null : user.getUsername());
    }

    @GetMapping("/bookmark")
    public StorePagedResponseDto getStoreInRangeHighBookmark(@RequestParam(required = false) BigDecimal lat,
                                                                  @RequestParam(required = false) BigDecimal lng,
                                                                  @RequestParam(required = false) String category,
                                                                  @RequestParam(required = false) List<String> facility,
                                                                  @PageableDefault Pageable pageable,
                                                                  @AuthenticationPrincipal User user) {
        return storeService.getStoreInRangeHighBookmark(lat, lng, category, facility, pageable, user == null ? null : user.getUsername());
    }

    @ApiOperation(value = "업장 상세정보를 조회하는 메소드")
    @GetMapping("/detail/{storeId}")
    public StoreCardResponseDto getStore(
            @PathVariable(required = false) int storeId,
            HttpServletRequest request,
            @AuthenticationPrincipal User user) {

        // TODO 토큰 유효성 검사하기
        AuthToken authToken = tokenProvider.convertAccessToken(request);
        if (!authToken.tokenValidate()) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        String socialId = "";
        if (user != null) {
            socialId = user.getUsername();
        }

        return storeService.getStore(storeId, socialId);
    }

    @ApiOperation(value = "DB에 존재하는 시/군/구 정보를 조회하는 메소드")
    @GetMapping("/location/{sido}/sigungu")
    public void getSigungu(@PathVariable(required = false) String sido) {

    }
}