package com.mpnp.baechelin.store.controller;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.mpnp.baechelin.store.dto.StorePagedResponseDto;
import com.mpnp.baechelin.store.dto.StoreResponseDto;
import com.mpnp.baechelin.store.repository.StoreQueryRepository;
import com.mpnp.baechelin.store.service.StoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"매장 리스트를 반환하는 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @ApiOperation(value = "조건에 맞는 업장 목록을 반환하는 메소드")
    @GetMapping("/near")
//    public List<StoreCardResponseDto> getStoreInRange(@RequestParam(required = false) BigDecimal latStart,
    public StorePagedResponseDto getStoreInRange(@RequestParam(required = false) BigDecimal latStart,
                                                 @RequestParam(required = false) BigDecimal latEnd,
                                                 @RequestParam(required = false) BigDecimal lngStart,
                                                 @RequestParam(required = false) BigDecimal lngEnd,
                                                 @RequestParam(required = false) String category,
                                                 @RequestParam(required = false) List<String> facility,
                                                 @PageableDefault Pageable pageable,
                                                 @AuthenticationPrincipal User user) {
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
    public List<StoreCardResponseDto> getStoreInRangeHighBookmark(@RequestParam(required = false) BigDecimal lat,
                                                                  @RequestParam(required = false) BigDecimal lng,
                                                                  @RequestParam(required = false) String category,
                                                                  @RequestParam(required = false) List<String> facility,
                                                                  @RequestParam int limit,
                                                                  @AuthenticationPrincipal User user) {
        return storeService.getStoreInRangeHighBookmark(lat, lng, category, facility, limit, user == null ? null : user.getUsername());
    }
}