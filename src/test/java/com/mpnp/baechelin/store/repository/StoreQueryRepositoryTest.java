package com.mpnp.baechelin.store.repository;

import com.mpnp.baechelin.store.domain.Store;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class StoreQueryRepositoryTest {

    @Autowired
    private StoreQueryRepository storeQueryRepository;


    @Test
    public void mysqlFullTextMath() {
        String sido = null;
        String sigungu = "강남구";
        String keyword = "갈비";

        List<Store> stores = storeQueryRepository.searchStores(sido, sigungu, keyword);

        for (Store store : stores) {
            System.out.println(store.getAddress());
        }
    }

}