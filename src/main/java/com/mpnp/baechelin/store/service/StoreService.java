package com.mpnp.baechelin.store.service;

import com.mpnp.baechelin.store.dto.StoreResponseDto;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public List<StoreResponseDto> getStoreList() {
        return null;
    }
}
