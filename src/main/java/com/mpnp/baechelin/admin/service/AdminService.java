package com.mpnp.baechelin.admin.service;

import com.mpnp.baechelin.admin.dto.AdminResponseDto;
import com.mpnp.baechelin.store.domain.UserRegisterStore;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.mpnp.baechelin.store.repository.UserRegisterStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRegisterStoreRepository userRegisterStoreRepository;

    public List<AdminResponseDto> getUserRegisterStore() {
        Page<UserRegisterStore> foundUserRegisterStores = userRegisterStoreRepository.findAll(Pageable.ofSize(5));

        return foundUserRegisterStores.stream().map(AdminResponseDto::new).collect(Collectors.toList());
    }
}
