package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.service.LocationService;
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
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/convert/geo")
    public Map<String, Object> getAddressByGeo(@RequestParam BigDecimal lat, @RequestParam BigDecimal lng) {
        return locationService.convertGeoToAddress(String.valueOf(lat), String.valueOf(lng));
    }

    @GetMapping("/convert/address")
    public Map<String, Object> getGeoByAddress(@RequestParam String address) {
        return locationService.convertAddressToGeo(address);
    }

    @GetMapping("/convert/keyword")
    public Map<String, Object> getKeywordByGeoAndStoreName(@RequestParam BigDecimal lat, @RequestParam BigDecimal lng, String storeName){
        return locationService.convertGeoAndStoreNameToKeyword(String.valueOf(lat), String.valueOf(lng), storeName);
    }
}