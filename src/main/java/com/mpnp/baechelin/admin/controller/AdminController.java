package com.mpnp.baechelin.admin.controller;

import com.mpnp.baechelin.admin.dto.AdminResponseDto;
import com.mpnp.baechelin.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/approval")
    public List<AdminResponseDto> getUserRegisterStore() {
        return adminService.getUserRegisterStore();
    }
}
