package com.mpnp.baechelin;

import com.mpnp.baechelin.common.httpclient.HttpConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = { HttpConfig.class })
class BaechelinApplicationTests {

	@Test
	void contextLoads() {
	}

}
