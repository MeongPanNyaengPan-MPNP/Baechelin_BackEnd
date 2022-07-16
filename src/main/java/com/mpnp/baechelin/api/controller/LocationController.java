package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/map")
public class LocationController {
    private final LocationService locationService;
    @GetMapping
    public String getAddressByLocation(@RequestParam BigDecimal lat, @RequestParam BigDecimal lng){
        return locationService.convertLatLngToAddressWithRestTemplate(String.valueOf(lat), String.valueOf(lng));
    }
}