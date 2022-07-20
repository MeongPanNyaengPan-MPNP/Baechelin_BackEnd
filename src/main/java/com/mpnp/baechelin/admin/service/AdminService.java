package com.mpnp.baechelin.admin.service;

import com.mpnp.baechelin.admin.dto.AdminResponseDto;
import com.mpnp.baechelin.store.domain.UserRegisterStore;
import com.mpnp.baechelin.store.repository.UserRegisterStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRegisterStoreRepository userRegisterStoreRepository;

    public List<AdminResponseDto> getUserRegisterStore() {
//        Page<UserRegisterStore> foundUserRegisterStores = userRegisterStoreRepository.findAll(Pageable.ofSize(5));

        List<UserRegisterStore> foundUserRegisterStores = userRegisterStoreRepository.findAll();
        List<AdminResponseDto> result = new ArrayList<>();

        for (UserRegisterStore foundUserRegisterStore : foundUserRegisterStores) {
            AdminResponseDto adminResponseDto = new AdminResponseDto(foundUserRegisterStore);
            result.add(adminResponseDto);
        }

        return result;
    }
}
