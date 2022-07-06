package com.mpnp.baechelin.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpnp.baechelin.api.config.HttpConfig;
import com.mpnp.baechelin.api.model.LocationKeywordSearchForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class LocationService {
    // 카카오 API를 사용할 예정입니다
    // API KEY : 04940cceefec44d7adb62166b7971cd5
    private final String kakaokey = "04940cceefec44d7adb62166b7971cd5";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpConfig httpConfig;

    /**
     * @param keyword
     * @return GET /v2/local/search/address.${FORMAT} HTTP/1.1
     * Host: dapi.kakao.com
     * Authorization: KakaoAK ${REST_API_KEY}
     */
    public Map<String, Object> giveInfoByKeyword(String keyword) {
        Map<String, Object> LatLngMap = new ConcurrentHashMap<>();

        // client 기본설정
        WebClient client = WebClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                        .defaultUriVariables(Collections.singletonMap("url", "https://dapi.kakao.com/v2/local/search/keyword.json"))
                        .clientConnector(new ReactorClientHttpConnector(httpConfig.httpConfig())) // 위의 타임아웃 적용
                        .build();

        StringBuilder sb = new StringBuilder();

        sb.append(
                client.get().uri(uriBuilder
                                -> uriBuilder.queryParam("query", keyword)
                                .queryParam("category_group_code", "FD6") // 음식점으로 특정 - FD6
                                .build())
                        .header("Authorization", "KakaoAK 04940cceefec44d7adb62166b7971cd5")
                        .retrieve().bodyToMono(String.class).block());

        try {
            JsonNode jsonNode = objectMapper.readTree(sb.toString()).get("documents");
            if (jsonNode.size() < 1) {
                // 결과값이 없을 때 false put
                LatLngMap.put("message", false);
            } else {
                // 결과값이 있을 때 true put
                LatLngMap.put("message", true);
                LatLngMap.put("category", jsonNode.get(0).get("category_group_name").asText());
                LatLngMap.put("name", jsonNode.get(0).get("place_name").asText());
                LatLngMap.put("address", jsonNode.get(0).get("road_address_name").asText());
                LatLngMap.put("latitude", jsonNode.get(0).get("y").asText());
                LatLngMap.put("longitude", jsonNode.get(0).get("x").asText());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return LatLngMap;
    }


    public LocationKeywordSearchForm giveInfoByKeywordMono(String keyword) {
        WebClient client = WebClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                .defaultUriVariables(Collections.singletonMap("url", "https://dapi.kakao.com/v2/local/search/keyword.json"))
                .clientConnector(new ReactorClientHttpConnector(httpConfig.httpConfig())) // 위의 타임아웃 적용
                .build();
        return client.get().uri(uriBuilder
                        -> uriBuilder.queryParam("query", keyword)
                        .queryParam("category_group_code", "FD6") // 음식점으로 특정 - FD6
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "KakaoAK 04940cceefec44d7adb62166b7971cd5")
                .retrieve().bodyToMono(LocationKeywordSearchForm.class).block();
    }

    // TODO 위도 경도를 기반으로 키워드 검색하기(필요값 : 업장명, 위도 ,경도)
    public LocationKeywordSearchForm giveCategoryByLatLngKeyword(String lat, String lng, String storeName) {
        WebClient client = WebClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                .defaultUriVariables(Collections.singletonMap("url", "https://dapi.kakao.com/v2/local/search/keyword.json"))
                .clientConnector(new ReactorClientHttpConnector(httpConfig.httpConfig())) // 위의타임아웃 적용
                .build();
        return client.get().uri(uriBuilder
                        -> uriBuilder.queryParam("query", storeName)
                        .queryParam("category_group_code", "FD6") // 음식점으로 특정 - FD6
                        .queryParam("x",lng)//위도, 경도 지정
                        .queryParam("y",lat)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "KakaoAK 04940cceefec44d7adb62166b7971cd5")
                .retrieve().bodyToMono(LocationKeywordSearchForm.class).block();
    }



}
