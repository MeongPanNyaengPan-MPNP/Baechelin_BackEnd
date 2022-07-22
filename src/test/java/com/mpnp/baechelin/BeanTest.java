package com.mpnp.baechelin;

import com.mpnp.baechelin.api.service.LocationService;
import com.mpnp.baechelin.api.service.LocationServiceWC;
import com.mpnp.baechelin.config.AppConfig;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanTest {
    AnnotationConfigApplicationContext ac = new
            AnnotationConfigApplicationContext(AppConfig.class);
    @Test
    @DisplayName("빈 확인")
    public void findBeanByName() {
        LocationService locationService = ac.getBean("locationService",
                LocationService.class);
        Assertions.assertThat(locationService).isInstanceOf(LocationServiceWC.class);
    }
}
