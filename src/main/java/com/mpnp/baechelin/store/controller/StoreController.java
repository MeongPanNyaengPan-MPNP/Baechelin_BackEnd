package com.mpnp.baechelin.store.controller;

import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreResponseDto;
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
        List<StoreResponseDto> storeList = storeService.getStoreList();
        return storeList;
    }

    @GetMapping("/near")
    public List<StoreResponseDto> giveStoreInRange(@RequestParam BigDecimal latStart,
                                                   @RequestParam BigDecimal latEnd,
                                                   @RequestParam BigDecimal lngStart,
                                                   @RequestParam BigDecimal lngEnd,
                                                   // sort 기준 정하기
                                                   //@PageableDefault(sort = {""}, direction = Sort.Direction.DESC) Pageable pageable){
                                                   @PageableDefault Pageable pageable) {
        List<Store> betweenLngLat = storeQueryRepository.findBetweenLngLat(latStart, latEnd, lngStart, lngEnd, pageable);
        return betweenLngLat.stream().map(storeService::storeToResDto).collect(Collectors.toList());
    }
}
