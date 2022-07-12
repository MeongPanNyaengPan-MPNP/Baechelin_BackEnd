package com.mpnp.baechelin.store.controller;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreResponseDto;
import com.mpnp.baechelin.store.repository.StoreQueryRepository;
import com.mpnp.baechelin.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    private final StoreQueryRepository storeQueryRepository;

    @GetMapping("/near")
    public List<StoreResponseDto> getStoreInRange(@RequestParam(required = false) BigDecimal latStart,
                                                  @RequestParam(required = false) BigDecimal latEnd,
                                                  @RequestParam(required = false) BigDecimal lngStart,
                                                  @RequestParam(required = false) BigDecimal lngEnd,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(required = false) List<String> facility,
                                                  @PageableDefault Pageable pageable) {
        List<Store> betweenLngLat = storeQueryRepository.findBetweenLngLat(latStart, latEnd, lngStart, lngEnd, category, facility, pageable);
        return betweenLngLat.parallelStream().map(storeService::storeToResDto).collect(Collectors.toList());// 순서보장
    }
    @GetMapping("/point")
    public List<StoreResponseDto> getStoreInRangeHighPoint(@RequestParam(required = false) BigDecimal latStart,
                                                           @RequestParam(required = false) BigDecimal latEnd,
                                                           @RequestParam(required = false) BigDecimal lngStart,
                                                           @RequestParam(required = false) BigDecimal lngEnd,
                                                           @RequestParam(required = false) String category,
                                                           @RequestParam(required = false) List<String> facility,
                                                           @PageableDefault Pageable pageable) {
        List<Store> betweenLngLat = storeQueryRepository.findStoreOrderByPoint(latStart, latEnd, lngStart, lngEnd, category, facility, pageable);
        return betweenLngLat.parallelStream().map(storeService::storeToResDto).collect(Collectors.toList());// 순서보장
    }

    @GetMapping("/bookmark")
    public List<StoreResponseDto> getStoreInRangeHighBookmark(@RequestParam(required = false) BigDecimal latStart,
                                                              @RequestParam(required = false) BigDecimal latEnd,
                                                              @RequestParam(required = false) BigDecimal lngStart,
                                                              @RequestParam(required = false) BigDecimal lngEnd,
                                                              @RequestParam(required = false) String category,
                                                              @RequestParam(required = false) List<String> facility,
                                                              @RequestParam int limit) {
        List<Store> betweenLngLat = storeQueryRepository.findStoreOrderByBookmark(latStart, latEnd, lngStart, lngEnd, category, facility, limit);
        return betweenLngLat.parallelStream().map(storeService::storeToResDto).collect(Collectors.toList());// 순서보장
    }
}
