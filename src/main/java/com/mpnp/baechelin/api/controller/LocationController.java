package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.repository.MapQueryRepository;
import com.mpnp.baechelin.api.service.LocationService;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/map")
public class LocationController {
    private final LocationService locationService;
    private final MapQueryRepository mapQueryRepository;
    private final StoreRepository storeRepository;

    // TODO 위도, 경도를 두 개 받아 해당 범위 안에 있는 Store 리턴하기
    @GetMapping
    public List<Store> giveStoreInRange(@RequestParam BigDecimal latStart,
                                        @RequestParam BigDecimal latEnd,
                                        @RequestParam BigDecimal lngStart,
                                        @RequestParam BigDecimal lngEnd,
                                        // sort 기준 정하기
                                        //@PageableDefault(sort = {""}, direction = Sort.Direction.DESC) Pageable pageable){
                                        @PageableDefault Pageable pageable) {
        // TODO 페이징 적용
        log.info("latStart : {}", latStart); // 위도
        log.info("latEnd : {}", latEnd); // 위도
        log.info("lngStart : {}", lngStart); // 경도
        log.info("lngEnd : {}", lngEnd); // 경도
        return locationService.giveStoresByRange(latStart,latEnd,lngStart,lngEnd,pageable);
    }
    @GetMapping("/test")
    public List<Store> storeTest(){
        return storeRepository.findAll();
    }


}