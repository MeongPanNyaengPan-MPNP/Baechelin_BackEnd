package com.mpnp.baechelin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@PropertySources({@PropertySource("classpath:application-key.properties")})
public class BaechelinApplication {
	public static void main(String[] args) {
		SpringApplication.run(BaechelinApplication.class, args);
	}

}