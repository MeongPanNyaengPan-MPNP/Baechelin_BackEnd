package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class LocationController {
    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<?> callMap(@RequestParam String keyword) {
        //return ResponseEntity.ok().body(mapService.giveInfoByKeyword(keyword));
        return ResponseEntity.ok().body(locationService.giveLatLngByAddress(keyword));
    }

}