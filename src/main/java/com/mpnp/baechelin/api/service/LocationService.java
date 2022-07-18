package com.mpnp.baechelin.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mpnp.baechelin.api.model.LocationAddressSearchForm;
import com.mpnp.baechelin.config.httpclient.HttpConfig;
import com.mpnp.baechelin.api.model.LocationKeywordSearchForm;
import com.mpnp.baechelin.store.domain.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LocationService {
    private final HttpConfig httpConfig;

    /**
     * @param address 주소
     * @return LocationKeywordSearchForm의 규격에 맞는 결과 하나를 가져옴
     */
    public LocationKeywordSearchForm giveLatLngByAddress(String address) {
        WebClient client = WebClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                .defaultUriVariables(Collections.singletonMap("url", "https://dapi.kakao.com/v2/local/search/keyword.json"))
                .clientConnector(new ReactorClientHttpConnector(httpConfig.httpClient())) // 위의 타임아웃 적용
                .build();
        return client.get().uri(uriBuilder
                        -> uriBuilder.queryParam("query", address)
//                        .queryParam("category_group_code", "FD6") // 음식점으로 특정 - FD6
                        .queryParam("page", 1)
                        .queryParam("size", 1)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "KakaoAK 04940cceefec44d7adb62166b7971cd5")
                .retrieve().bodyToMono(LocationKeywordSearchForm.class).flux()
                .toStream()
                .findFirst()
                .orElse(null);
    }

    // TODO 위도 경도를 기반으로 키워드 검색하기(필요값 : 업장명, 위도 ,경도)
    public LocationKeywordSearchForm giveCategoryByLatLngKeyword(String lat, String lng, String storeName) {
        WebClient client = WebClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                .defaultUriVariables(Collections.singletonMap("url", "https://dapi.kakao.com/v2/local/search/keyword.json"))
                .clientConnector(new ReactorClientHttpConnector(httpConfig.httpClient())) // 위의타임아웃 적용
                .build();
        return client.get().uri(uriBuilder
                        -> uriBuilder.queryParam("query", storeName)
//                        .queryParam("category_group_code", "FD6") // 음식점으로 특정 - FD6
                        .queryParam("x", lng)//위도, 경도 지정
                        .queryParam("y", lat)
                        .queryParam("radius", 200)
                        .queryParam("page", 1)
                        .queryParam("size", 1)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "KakaoAK 04940cceefec44d7adb62166b7971cd5")
                .retrieve().bodyToMono(LocationKeywordSearchForm.class)
                .flux()
                .toStream().findFirst()
                .orElse(null);
    }

    /**
     * RestTemplate으로 위도, 경도 받아오기
     */
    public Map<String, Object> convertAddressToGeo(String address) {
        Map<String, Object> map = new HashMap<>();
        // status?, latitude, longitude 를 키로 가지는 HashMap 생성
        LocationKeywordSearchForm locationKeywordSearchForm = giveLatLngByAddress(address);
//        latLngDoc.getY()
        if (locationKeywordSearchForm == null) {
            map.put("status", false);
        } else {
            LocationKeywordSearchForm.Documents latLngDoc
                    = Arrays.stream(locationKeywordSearchForm.getDocuments()).findAny().orElse(null);
            if (latLngDoc != null) {
                map.put("latitude", latLngDoc.getY());
                map.put("longitude", latLngDoc.getX());
                map.put("status", true);
            } else {
                map.put("status", false);
            }
        }
        return map;
    }

    public LocationKeywordSearchForm giveLatLngByAddressRest(String address) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "KakaoAK 04940cceefec44d7adb62166b7971cd5");
        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("query", address)
                .queryParam("page", 1)
                .queryParam("size", 1)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<LocationKeywordSearchForm> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), LocationKeywordSearchForm.class
        );
        return resultRe.getBody();
    }

    public LocationKeywordSearchForm giveCategoryByLatLngKeywordRest(String lat, String lng, String storeName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "KakaoAK 04940cceefec44d7adb62166b7971cd5");
        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("query", storeName)
                .queryParam("x", lng)//위도, 경도 지정
                .queryParam("y", lat)
                .queryParam("radius", 200)
                .queryParam("page", 1)
                .queryParam("size", 1)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<LocationKeywordSearchForm> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), LocationKeywordSearchForm.class
        );
        return resultRe.getBody();

    }

    public Map<String, Object> convertGeoAndStoreNameToKeyword(String lat, String lng, String storeName) {
        Map<String, Object> map = new HashMap<>();
        // status?, latitude, longitude 를 키로 가지는 HashMap 생성
        LocationKeywordSearchForm locationKeywordSearchForm = giveCategoryByLatLngKeywordRest(lat, lng, storeName);
//        latLngDoc.getY()
        if (locationKeywordSearchForm == null) {
            map.put("status", false);
        } else {
            LocationKeywordSearchForm.Documents latLngDoc
                    = Arrays.stream(locationKeywordSearchForm.getDocuments()).findFirst().orElse(null);
            if (latLngDoc != null) {
                map.put("category", categoryFilter(latLngDoc.getCategory_name()));
                map.put("storeId", Integer.parseInt(latLngDoc.getId()));
                map.put("storeName", latLngDoc.getPlace_name());
                map.put("status", true);
            } else {
                map.put("status", false);
            }
        }
        return map;
    }

    private String categoryFilter(String category) {
        if (category == null) {
            return Category.ETC.getDesc();
        } else if (category.contains(">")) {
            return Category.giveCategory(category.split(" > ")[1]).getDesc();
        } else {
            return null;
        }
    }

    public Map<String, Object> convertGeoToAddress(String lat, String lng) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "KakaoAK 04940cceefec44d7adb62166b7971cd5");
        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/geo/coord2address.json")
                .queryParam("x", lng)//위도, 경도 지정
                .queryParam("y", lat)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<LocationAddressSearchForm> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), LocationAddressSearchForm.class
        );
        LocationAddressSearchForm locationKeywordSearchForm = resultRe.getBody();
        Map<String, Object> map = new HashMap<>();
        if (locationKeywordSearchForm == null) {
            map.put("status", false);
        } else {
            LocationAddressSearchForm.TotalAddress address = Arrays.stream(locationKeywordSearchForm.getDocuments()).findFirst().orElse(null);
            if (address != null) {
                map.put("address", address.getAddress().getAddress_name());
                map.put("status", true);
            } else {
                map.put("status", false);
            }
        }
        return map;
    }
}
