package com.mpnp.baechelin.map.service;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MapService {
    // 카카오 API를 사용할 예정입니다
    // API KEY : 04940cceefec44d7adb62166b7971cd5
    public Map<String, Object> giveLatLong(String keyword){
        Map<String, Object> returnMap = new HashMap<>();
        // 타임아웃 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        // client 기본설정
        WebClient client = WebClient.builder()
                .baseUrl("http://openapi.seoul.go.kr:8088")
//                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://openapi.seoul.go.kr:8088"))
                .clientConnector(new ReactorClientHttpConnector(httpClient)) // 위의 타임아웃 적용
                .build();
    }
}
