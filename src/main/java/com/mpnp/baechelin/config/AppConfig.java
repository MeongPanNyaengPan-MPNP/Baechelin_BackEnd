package com.mpnp.baechelin.config;

import com.mpnp.baechelin.api.service.LocationService;
import com.mpnp.baechelin.api.service.LocationServiceRT;
import com.mpnp.baechelin.api.service.LocationServiceWC;
import com.mpnp.baechelin.common.httpclient.HttpConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public LocationService locationService(){
        return new LocationServiceRT();
//        return new LocationServiceWC(new HttpConfig());
    }

}
