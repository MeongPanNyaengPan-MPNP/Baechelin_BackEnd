package com.mpnp.baechelin.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.mpnp.baechelin.store.dto.StoreDetailResponseDto;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;

@EnableCaching
@Configuration
@Profile("!test")
public class RedisCacheConfig {

    private final RedisConnectionFactory redisConnectionFactory;
    private final ObjectMapper objectMapper;

    public RedisCacheConfig(
            RedisConnectionFactory redisConnectionFactory,
            ObjectMapper objectMapper
    ) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.objectMapper = objectMapper;
    }

    public CacheManager redisCacheManager() {
        CollectionType collectionType = objectMapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, StoreCardResponseDto.class);

        RedisCacheConfiguration redisCachingConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new Jackson2JsonRedisSerializer<>(
                                        collectionType
                                )
                        )
                )
                .entryTtl(Duration.ofMinutes(30));

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCachingConfiguration)
                .build();
    }
}
