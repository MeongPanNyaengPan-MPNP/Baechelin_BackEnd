package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.service.LocationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@ApiOperation(value="카카오톡 API를 통해 정보를 얻는 컨트롤러")
public class LocationController {
    private final LocationService locationService;
    @ApiOperation(value="위도, 경도를 주소로 바꾸는 함수")
    @GetMapping("/convert/geo")
    public Map<String, Object> getAddressByGeo(@RequestParam BigDecimal lat, @RequestParam BigDecimal lng) {
        return locationService.convertGeoToAddressRT(String.valueOf(lat), String.valueOf(lng));
    }

    @ApiOperation(value="주소를 위도, 경도로 바꾸는 함수")
    @GetMapping("/convert/address")
    public Map<String, Object> getGeoByAddress(@RequestParam String address) {
        return locationService.convertAddressToGeo(address);
    }

    @ApiOperation(value="위도, 경도, 업장명을 통해 업장 정보를 받아오는 함수")
    @GetMapping("/convert/keyword")
    public Map<String, Object> getKeywordByGeoAndStoreName(@RequestParam BigDecimal lat, @RequestParam BigDecimal lng, String storeName){
        return locationService.convertGeoAndStoreNameToKeyword(String.valueOf(lat), String.valueOf(lng), storeName);
    }
}