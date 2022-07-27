package com.mpnp.baechelin.util;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class Nginx {
    private final Environment environment;

    @GetMapping("/health")
    public String healthCheck() {
        return "Health Check V7";
    }

    @GetMapping
    public String main(){
        return "Hello World!";
    }
}
