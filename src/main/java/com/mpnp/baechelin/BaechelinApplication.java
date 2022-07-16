package com.mpnp.baechelin;

import com.mpnp.baechelin.config.properties.AppProperties;
import com.mpnp.baechelin.config.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@EnableConfigurationProperties({
        CorsProperties.class,
        AppProperties.class
})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@PropertySources({@PropertySource("classpath:application-key.properties")})
public class BaechelinApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaechelinApplication.class, args);
    }
}