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

    @GetMapping
    public List<StoreResponseDto> getStoreList() {
        return storeService.getStoreList();
    }

    @GetMapping("/near")
    public List<StoreResponseDto> giveStoreInRange(@RequestParam BigDecimal latStart,
                                                   @RequestParam BigDecimal latEnd,
                                                   @RequestParam BigDecimal lngStart,
                                                   @RequestParam BigDecimal lngEnd,
                                                   @RequestParam(required = false) String category,
                                                   @RequestParam(required = false) List<String> facility,
                                                   @PageableDefault Pageable pageable) {
        List<Store> betweenLngLat = storeQueryRepository.findBetweenLngLat(latStart, latEnd, lngStart, lngEnd, category, facility, pageable);
        return betweenLngLat.parallelStream().map(storeService::storeToResDto).collect(Collectors.toList());// 순서보장
    }
    @GetMapping("/near/high-point")
    public List<StoreResponseDto> giveStoreInRangeHighPoint(@RequestParam BigDecimal latStart,
                                                   @RequestParam BigDecimal latEnd,
                                                   @RequestParam BigDecimal lngStart,
                                                   @RequestParam BigDecimal lngEnd,
                                                   @RequestParam(required = false) String category,
                                                   @RequestParam(required = false) List<String> facility,
                                                   @PageableDefault Pageable pageable) {
        List<Store> betweenLngLat = storeQueryRepository.findBetweenLngLat(latStart, latEnd, lngStart, lngEnd, category, facility, pageable);
        return betweenLngLat.parallelStream().map(storeService::storeToResDto).collect(Collectors.toList());// 순서보장
    }

    @GetMapping("/near/high-bookmark")
    public List<StoreResponseDto> giveStoreInRangeHighBookmark(@RequestParam BigDecimal latStart,
                                                            @RequestParam BigDecimal latEnd,
                                                            @RequestParam BigDecimal lngStart,
                                                            @RequestParam BigDecimal lngEnd,
                                                            @RequestParam(required = false) String category,
                                                            @RequestParam(required = false) List<String> facility,
                                                            @RequestParam int limit) {
        List<Store> betweenLngLat = storeQueryRepository.findStoreOrderByBookmark(latStart, latEnd, lngStart, lngEnd, category, facility, limit);
        return betweenLngLat.parallelStream().map(storeService::storeToResDto).collect(Collectors.toList());// 순서보장
    }
}
