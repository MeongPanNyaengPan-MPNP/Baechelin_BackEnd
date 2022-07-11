package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.review.domain.QReview;
import com.mpnp.baechelin.store.domain.QStoreImage;
import com.mpnp.baechelin.store.domain.Store;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static com.mpnp.baechelin.review.domain.QReview.review1;
import static com.mpnp.baechelin.store.domain.QStore.store;
import static com.mpnp.baechelin.store.domain.QStoreImage.storeImage;

@Repository
@Transactional
public class StoreQueryRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public StoreQueryRepository(JPAQueryFactory  queryFactory) {
        super(Store.class);
        this.queryFactory = queryFactory;
    }


    public List<Store> findBetweenLngLat(BigDecimal latStart,
                                         BigDecimal latEnd,
                                         BigDecimal lngStart,
                                         BigDecimal lngEnd,
                                         Pageable pageable) {
        return queryFactory.selectFrom(store)
                .where(store.latitude.goe(latStart),
                        store.latitude.loe(latEnd),
                        store.longitude.goe(lngStart),
                        store.longitude.loe(lngEnd))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }
}
