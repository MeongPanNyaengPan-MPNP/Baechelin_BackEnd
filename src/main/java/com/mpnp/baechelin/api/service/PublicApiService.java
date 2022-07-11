package com.mpnp.baechelin.api.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpnp.baechelin.config.httpclient.HttpConfig;
import com.mpnp.baechelin.api.dto.*;
import com.mpnp.baechelin.api.model.LocationKeywordSearchForm;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PublicApiService {
    /**
     * @param apiRequestDto : 유저가 등록하는 업소 정보들을 담은 DTO
     * @return ApiResponseDto - 응답 형태에 맞는 객체 반환
     * @throws IOException
     */
    private final StoreRepository storeRepository;
    private final LocationService locationService;
    private final HttpConfig httpConfig;
    ObjectMapper objectMapper = new ObjectMapper();

    public PublicApiResponseDto processApiToDBWithWebclientMono(PublicApiRequestDto publicApiRequestDto) throws UnsupportedEncodingException {
        WebClient client = WebClient.builder()
                .baseUrl("http://openapi.seoul.go.kr:8088")
//                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://openapi.seoul.go.kr:8088"))
                .clientConnector(new ReactorClientHttpConnector(httpConfig.httpClient())) // 위의 타임아웃 적용
                .build();

        String key = URLEncoder.encode(publicApiRequestDto.getKey(), "UTF-8"); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
        String type = URLEncoder.encode(publicApiRequestDto.getType(), "UTF-8"); /*요청파일타입 (xml,xmlf,xls,json) */
        String service = URLEncoder.encode(publicApiRequestDto.getService(), "UTF-8"); /*서비스명 (대소문자 구분 필수입니다.)*/
        String start = URLEncoder.encode(String.valueOf(publicApiRequestDto.getStartIndex()), "UTF-8"); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
        String end = URLEncoder.encode(String.valueOf(publicApiRequestDto.getEndIndex()), "UTF-8"); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/

        PublicApiResponseDto result = client.get().uri(
                        uriBuilder -> uriBuilder.pathSegment(key, type, service, start, end).path("/")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    throw new IllegalAccessError("400");
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    throw new IllegalAccessError("500");
                })
                .bodyToMono(PublicApiResponseDto.class).flux()
                .toStream()
                .findFirst()
                .orElse(null);
        if (result == null) {
            return null;
        }
        setInfos(result);
        saveDTO(result.getTouristFoodInfo().getRow());
        return result;

    }

    public PublicApiResponseDto processApiToDBWithRestTemplate(PublicApiRequestDto publicApiRequestDto) throws UnsupportedEncodingException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        URI uri = UriComponentsBuilder
                .fromUriString("http://openapi.seoul.go.kr:8088")
                .path("/{key}/{type}/{service}/{start}/{end}")
                .buildAndExpand(publicApiRequestDto.getKey(), publicApiRequestDto.getType(), publicApiRequestDto.getService(), publicApiRequestDto.getStartIndex(), publicApiRequestDto.getEndIndex())
                .encode()
                .toUri();
        RestTemplate restTemplate = new RestTemplate();
        log.warn(uri.toString());
        ResponseEntity<PublicApiResponseDto> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), PublicApiResponseDto.class
        );
        PublicApiResponseDto result = resultRe.getBody();
        if (result == null) {
            return null;
        }
        setInfos(result);
        saveDTO(result.getTouristFoodInfo().getRow());
        return result;
    }

    private void setInfos(PublicApiResponseDto publicApiResponseDto) {
        publicApiResponseDto.getTouristFoodInfo().getRow().forEach(row -> {
                    try {
                        if (!setRowLngLat(row)) return;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        setRowCategoryAndId(row);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        saveDTO(publicApiResponseDto.getTouristFoodInfo().getRow());
    }


    private boolean setRowLngLat(PublicApiResponseDto.Row row) throws JsonProcessingException {
        LocationKeywordSearchForm latLngSearchForm = locationService.giveLatLngByAddressRest(row.getADDR());
//        LocationKeywordSearchForm latLngSearchForm = locationService.giveLatLngByAddress(row.getADDR());
        if (latLngSearchForm == null) return false;
        LocationKeywordSearchForm.Documents latLngDoc = Arrays.stream(latLngSearchForm.getDocuments()).findFirst().orElse(null);
        if (latLngDoc == null)
            return false;
        row.setLatitude(latLngDoc.getY());
        row.setLongitude(latLngDoc.getX());
        // 카테고리 ENUM으로 전환하기
        row.setCategory(categoryFilter(Optional.of(latLngDoc.getCategory_name()).orElse("기타")));
        return true;
    }

    private void setRowCategoryAndId(PublicApiResponseDto.Row row) throws JsonProcessingException {
        LocationKeywordSearchForm categorySearchForm = locationService.giveCategoryByLatLngKeywordRest(row.getLatitude(), row.getLongitude(), row.getSISULNAME());
//        LocationKeywordSearchForm categorySearchForm = locationService.giveCategoryByLatLngKeyword(row.getLatitude(), row.getLongitude(), row.getSISULNAME());
        LocationKeywordSearchForm.Documents categoryDoc = Arrays.stream(categorySearchForm.getDocuments()).findFirst().orElse(null);
        if (categoryDoc == null || !Arrays.asList("FD6", "CE7").contains(categoryDoc.getCategory_group_code()))
            return;
        row.setStoreId(categoryDoc.getId());
        row.setSISULNAME(categoryDoc.getPlace_name());
        row.setCategory(categoryFilter(Optional.of(categoryDoc.getCategory_name()).orElse(null)));
    }


    private void saveDTO(List<PublicApiResponseDto.Row> rows) {
        List<Store> storeList = rows.stream().filter(this::storeValidation)
                .map(Store::new).collect(Collectors.toList());
        // storeRepository 구현 시 save 호출하기
        for (Store store : storeList) {
//            log.debug("miniRow print : {}", store.toString());
            if (!storeRepository.existsById(store.getId())) {
                storeRepository.save(store);
            }
        }
    }

    private String categoryFilter(String category) {
        if (category == null) {
            return "기타";
        } else if (category.contains(">")) {
            return category.split(" > ")[1];
        } else {
            return null;
        }
    }

    private boolean storeValidation(PublicApiResponseDto.Row row) {
        return row.getLatitude() != null && row.getLongitude() != null && row.getCategory() != null && row.getStoreId() != null;
    }

}
