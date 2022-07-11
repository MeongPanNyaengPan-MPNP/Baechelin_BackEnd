package com.mpnp.baechelin.api.controller;

import com.mpnp.baechelin.api.service.LocationService;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/map")
public class LocationController {
    private final LocationService locationService;
    private final StoreRepository storeRepository;

}