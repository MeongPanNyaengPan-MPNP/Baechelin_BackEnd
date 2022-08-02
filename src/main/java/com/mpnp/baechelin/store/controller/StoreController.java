package com.mpnp.baechelin.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.login.jwt.AuthToken;
import com.mpnp.baechelin.login.jwt.AuthTokenProvider;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreDetailResponseDto;
import com.mpnp.baechelin.store.dto.StorePagedResponseDto;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.store.service.StoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Api(tags = {"매장 리스트를 반환하는 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
@Validated
@Slf4j
public class StoreController {

    private final StoreService storeService;
    private final AuthTokenProvider tokenProvider;

    @ApiOperation(value = "조건에 맞는 업장 목록을 반환하는 메소드")
    @GetMapping("/near")
    public StorePagedResponseDto getStoreInRange(@RequestParam(required = false) BigDecimal lat,
                                                 @RequestParam(required = false) BigDecimal lng,
                                                 @RequestParam(required = false) String category,
                                                 @RequestParam(required = false) List<String> facility,
                                                 @PageableDefault Pageable pageable,
                                                 @AuthenticationPrincipal User user) {
        return storeService.getStoreInOnePointRange(lat, lng, category, facility, pageable, user == null ? null : user.getUsername());
    }

    @ApiOperation(value = "지도에서 조건에 맞는 업장 목록을 반환하는 메소드")
    @GetMapping("/near-map")
    public StorePagedResponseDto getStoreInRange(@RequestParam(required = false) BigDecimal latStart,
                                                 @RequestParam(required = false) BigDecimal latEnd,
                                                 @RequestParam(required = false) BigDecimal lngStart,
                                                 @RequestParam(required = false) BigDecimal lngEnd,
                                                 @RequestParam(required = false) String category,
                                                 @RequestParam(required = false) List<String> facility,
                                                 @PageableDefault Pageable pageable,
                                                 @AuthenticationPrincipal User user) {
        return storeService.getStoreInTwoPointRange(latStart, latEnd, lngStart, lngEnd, category, facility, pageable, user == null ? null : user.getUsername());
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
    @Cacheable(value="store", key="#storeId", cacheManager = "cacheManager")
    @ApiOperation(value = "업장 상세정보를 조회하는 메소드")
    @GetMapping("/detail/{storeId}")
    public StoreDetailResponseDto getStore(
            @PathVariable(required = false) int storeId,
            HttpServletRequest request,
            @AuthenticationPrincipal User user) {

        AuthToken authToken = tokenProvider.convertAccessToken(request);

        if (authToken != null && !authToken.tokenValidate()) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        return storeService.getStore(storeId, user == null ? null : user.getUsername());
    }

    @ApiOperation(value = "시/도 정보를 이용해 DB에 존재하는 시/군/구 정보를 조회하는 메소드")
    @GetMapping("/location/sigungu")
    public Map<String, List<String>> getSigungu(@RequestParam String sido) {
        return storeService.getSigungu(sido);
    }

    @ApiOperation(value = "시/도, 시/군/구, 검색어를 이용해 업장 리스트를 조회하는 메소드")
    @GetMapping("/search")
    public StorePagedResponseDto searchStoresByKeyword(
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String sigungu,
            @RequestParam(required = false) @Size(min = 2, message = "검색어는 두글자 이상 입력해주세요.") String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> facility,
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal User user) {

        if (StringUtils.isEmpty(sido) && StringUtils.isEmpty(sigungu) && StringUtils.isEmpty(keyword) && StringUtils.isEmpty(category) && ObjectUtils.isEmpty(facility)) {
            throw new CustomException(ErrorCode.KEYWORD_ARE_NEEDED);
        }

        return storeService.searchStores(sido, sigungu, keyword, category, facility, user == null ? null : user.getUsername(), pageable);
    }
}