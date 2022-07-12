package com.mpnp.baechelin.config;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

import static com.mpnp.baechelin.store.domain.QStore.store;

@Configuration
public class QuerydslConfiguration {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    public static void locationBuilder(BigDecimal latStart, BigDecimal latEnd, BigDecimal lngStart, BigDecimal lngEnd, BooleanBuilder builder) {
        builder.and(latStart == null ? null : store.latitude.goe(latStart));
        builder.and(latEnd == null ? null : store.latitude.loe(latEnd));
        builder.and(lngStart == null ? null : store.longitude.goe(lngStart));
        builder.and(lngEnd == null ? null : store.longitude.loe(lngEnd));
    }
}
