package com.mpnp.baechelin.api.service;

import com.mpnp.baechelin.api.dto.LocationInfoDto;
import com.mpnp.baechelin.api.dto.LocationPartDto;
import com.mpnp.baechelin.api.model.LocationAddressSearchForm;
import com.mpnp.baechelin.api.model.LocationKeywordSearchForm;
import com.mpnp.baechelin.common.httpclient.HttpConfig;
import com.mpnp.baechelin.store.domain.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LocationServiceWC implements LocationService {
    private final HttpConfig httpConfig;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;


    /**
     * @param address 주소
     * @return 위도, 경도, Status를 가지는 Map 반환
     * 주소 -> 위도/경도 이므로 <<1개 반환>>
     * Controller, Service에서 같은 Map 형태로 사용
     */
    public LocationPartDto.LatLong convertAddressToGeo(String address) {
        // status, latitude, longitude 를 키로 가지는 HashMap 생성
        LocationPartDto.LatLong locLl = LocationPartDto.LatLong.builder().build();
        LocationKeywordSearchForm locationKeywordSearchForm = getLatLngByAddress(address);
        if (locationKeywordSearchForm == null) { // 비어 있을 때 status-false 저장
            return locLl;
        }
        LocationKeywordSearchForm.Documents latLngDoc
                = Arrays.stream(locationKeywordSearchForm.getDocuments()).findAny().orElse(null);
        if (latLngDoc != null) {
            locLl = LocationPartDto.LatLong.builder()
                    .latitude(latLngDoc.getY())
                    .longitude(latLngDoc.getX())
                    .status(true)
                    .build();
        }
        return locLl;
    }

    /**
     * @param address 주소
     * @return LocationKeywordSearchForm의 규격에 맞는 결과 하나를 가져옴
     */


    private LocationKeywordSearchForm getLatLngByAddress(String address) {
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
                .header("Authorization", kakaoApiKey)
                .retrieve().bodyToMono(LocationKeywordSearchForm.class).flux()
                .toStream()
                .findFirst()
                .orElse(null);
    }

    private LocationKeywordSearchForm getCategoryByLatLngKeyword(String lat, String lng, String keyword) {
        LocationKeywordSearchForm searchFormResult = getCategoryByCode(lat, lng, keyword, "FD6", 1);
        if (searchFormResult == null) {
            searchFormResult = getCategoryByCode(lat, lng, keyword, "CE7", 1);
        }
        return searchFormResult;
    }


    /**
     * @param lat       위도
     * @param lng       경도
     * @param storeName 업장명
     * @return 위도, 경도, 업장명을 만족하는 장소 찾기
     */
    private LocationKeywordSearchForm getCategoryByCode(String lat, String lng, String storeName, String cateCode, int page) {
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
                        .queryParam("category_group_code", cateCode)
                        .queryParam("radius", 20)
                        .queryParam("page", page)
                        .queryParam("size", 15)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", kakaoApiKey)
                .retrieve().bodyToMono(LocationKeywordSearchForm.class)
                .flux()
                .toStream().findFirst()
                .orElse(null);
    }


    public LocationInfoDto.LocationResponse convertGeoAndStoreNameToKeyword(String lat, String lng, String storeName) {
        LocationKeywordSearchForm locationKeywordSearchForm = getCategoryByLatLngKeyword(lat, lng, storeName);
        // 위도, 경도, 업장명을 가지고 업장 정보를 찾는다
        if (locationKeywordSearchForm == null) {
            return null;
        }
        LocationKeywordSearchForm.Documents latLngDoc
                = Arrays.stream(locationKeywordSearchForm.getDocuments()).findFirst().orElse(null);
        if (latLngDoc == null) {
            return null;
        }
        return LocationInfoDto.LocationResponse.builder()
                .storeId(Long.valueOf(latLngDoc.getId()))
                .latitude(latLngDoc.getY())
                .longitude(latLngDoc.getX())
                .category(categoryFilter(latLngDoc.getCategory_name()))
                .storeName(latLngDoc.getPlace_name())
                .phoneNumber(latLngDoc.getPhone()).build();
    }

    /**
     * @param lat     위도
     * @param lng     경도
     * @param address 주소
     * @return 업장명 대신에 주소를 입력해 해당 건물에 있는 업장을 배리어 프리 시설로 등록한다
     */

    @Override
    public List<LocationInfoDto.LocationResponse> convertGeoAndAddressToKeyword(String lat, String lng, String address) {
        List<LocationInfoDto.LocationResponse> resultList = new ArrayList<>();
        getStoreResults(lat, lng, address, "FD6", resultList);
        getStoreResults(lat, lng, address, "CE7", resultList);
        return resultList;
    }


    private void getStoreResults(String lat, String lng, String address, String type, List<LocationInfoDto.LocationResponse> resultList) {
        LocationKeywordSearchForm locationKeywordSearchForm;
        int page = 1;
        do {
            locationKeywordSearchForm = getCategoryByCode(lat, lng, address, type, page++);
            // 위도, 경도, 업장명을 가지고 업장 정보를 찾는다
            if (locationKeywordSearchForm == null) {
                return;
            }
            LocationKeywordSearchForm.Documents[] latLngDocArr = locationKeywordSearchForm.getDocuments();
            // 다음 페이지가 있는지 조사가 필요 - SearchForm에서 확인한다
            for (LocationKeywordSearchForm.Documents latLngDoc : latLngDocArr) {
                if (latLngDoc != null) {
                    LocationInfoDto.LocationResponse newResult = LocationInfoDto.LocationResponse.builder()
                            .latitude(latLngDoc.getY())
                            .longitude(latLngDoc.getX())
                            .category(categoryFilter(latLngDoc.getCategory_name()))
                            .storeName(latLngDoc.getPlace_name())
                            .storeId(Long.valueOf(latLngDoc.getId()))
                            .phoneNumber(latLngDoc.getPhone())
                            .build();
                    if (newResult.validate()) {
                        resultList.add(newResult);
                    }
                }
            }
        } while (locationKeywordSearchForm.getMeta().is_end()); // 마지막 페이지까지 검사
    }

    /**
     * @param category 변환할 카테고리
     * @return 카테고리의 중분류를 추출해 반환
     */
    private String categoryFilter(String category) {
        if (category == null) {
            return Category.ETC.getDesc();
        } else if (category.contains(">")) {
            return Category.giveCategory(category.split(" > ")[1]).getDesc();
        } else {
            return null;
        }
    }

    public LocationPartDto.Address convertGeoToAddress(String lat, String lng) {
        WebClient client = WebClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local/geo/coord2address.json")
                .defaultUriVariables(Collections.singletonMap("url", "https://dapi.kakao.com/v2/local/geo/coord2address.json"))
                .clientConnector(new ReactorClientHttpConnector(httpConfig.httpClient())) // 위의 타임아웃 적용
                .build();
        LocationAddressSearchForm locationAddressSearchForm = client.get().uri(uriBuilder
                        -> uriBuilder
                        .queryParam("x", lng)//위도, 경도 지정
                        .queryParam("y", lat)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", kakaoApiKey)
                .retrieve().bodyToMono(LocationAddressSearchForm.class).flux()
                .toStream()
                .findFirst()
                .orElse(null);
        return formToDto(locationAddressSearchForm);
    }

    private LocationPartDto.Address formToDto(LocationAddressSearchForm resultRe) {
        LocationPartDto.Address addressInfoDto = LocationPartDto.Address.builder().build();
        if (resultRe == null)
            return addressInfoDto;

        LocationAddressSearchForm.TotalAddress address = Arrays.stream(resultRe.getDocuments()).findFirst().orElse(null);
        if (address == null) {
            return addressInfoDto;
        } else {
            return LocationPartDto.Address.builder()
                    .address(address.getAddress().getAddress_name())
                    .status(true)
                    .build();
        }
    }
}
