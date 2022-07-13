package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.store.dto.StoreResponseDto;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreDetailService {

    private final StoreRepository storeRepository;

    public StoreResponseDto getStore(int storeId) {
        return null;
    }
}
