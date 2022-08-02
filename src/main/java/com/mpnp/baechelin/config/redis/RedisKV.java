package com.mpnp.baechelin.config.redis;

import lombok.Getter;

import java.time.Duration;
@Getter
public enum RedisKV {
    USER("user", Duration.ofSeconds(200)),
    STORE("store", Duration.ofSeconds(60));

    RedisKV(String name, Duration duration) {
        this.name = name;
        this.duration = duration;
    }

    private String name;
    private Duration duration;
}
