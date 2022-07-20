package com.mpnp.baechelin.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mpnp.baechelin.api.model.BarrierCode;
import com.mpnp.baechelin.api.model.PublicApiCategoryForm;
import com.mpnp.baechelin.api.model.PublicApiForm;
import com.mpnp.baechelin.api.dto.*;
import com.mpnp.baechelin.api.model.LocationKeywordSearchForm;
import com.mpnp.baechelin.common.httpclient.HttpConfig;
import com.mpnp.baechelin.store.domain.Category;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.ion.Decimal;

import javax.transaction.Transactional;
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
    private final StoreRepository storeRepository;
    private final LocationService locationService;
    private final StoreService storeService;
    @Value("${public.api.v1.key}")
    private String publicV1Key;
//    private final HttpConfig httpConfig;
//
//    public PublicApiResponseDto processApiToDBWithWebclientMono(PublicApiRequestDto publicApiRequestDto) throws UnsupportedEncodingException {
//        WebClient client = WebClient.builder()
//                .baseUrl("http://openapi.seoul.go.kr:8088")
////                .defaultCookie("cookieKey", "cookieValue")
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
//                .defaultUriVariables(Collections.singletonMap("url", "http://openapi.seoul.go.kr:8088"))
//                .clientConnector(new ReactorClientHttpConnector(httpConfig.httpClient())) // 위의 타임아웃 적용
//                .build();
//
//        String key = URLEncoder.encode(publicV1Key, "UTF-8"); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
//        String type = URLEncoder.encode(publicApiRequestDto.getType(), "UTF-8"); /*요청파일타입 (xml,xmlf,xls,json) */
//        String service = URLEncoder.encode(publicApiRequestDto.getService(), "UTF-8"); /*서비스명 (대소문자 구분 필수입니다.)*/
//        String start = URLEncoder.encode(String.valueOf(publicApiRequestDto.getStartIndex()), "UTF-8"); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
//        String end = URLEncoder.encode(String.valueOf(publicApiRequestDto.getEndIndex()), "UTF-8"); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/
//
//        PublicApiResponseDto result = client.get().uri(
//                        uriBuilder -> uriBuilder.pathSegment(key, type, service, start, end).path("/")
//                                .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .onStatus(HttpStatus::is4xxClientError, response -> {
//                    throw new IllegalAccessError("400");
//                })
//                .onStatus(HttpStatus::is5xxServerError, response -> {
//                    throw new IllegalAccessError("500");
//                })
//                .bodyToMono(PublicApiResponseDto.class).flux()
//                .toStream()
//                .findFirst()
//                .orElse(null);
//        if (result == null) {
//            return null;
//        }
//        setInfos(result);
//        saveDTO(result.getTouristFoodInfo().getRow());
//        return result;
//
//    }

    public void processApiV1(PublicApiRequestDto publicApiRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        URI uri = UriComponentsBuilder
                .fromUriString("http://openapi.seoul.go.kr:8088")
                .path("/{key}/{type}/{service}/{start}/{end}")
                .buildAndExpand(publicV1Key, publicApiRequestDto.getType(), publicApiRequestDto.getService(), publicApiRequestDto.getStartIndex(), publicApiRequestDto.getEndIndex())
                .encode()
                .toUri();
        RestTemplate restTemplate = new RestTemplate();
        log.warn(uri.toString());
        ResponseEntity<PublicApiResponseDto> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), PublicApiResponseDto.class
        );
        PublicApiResponseDto result = resultRe.getBody();
        if (result == null) {
            return;
        }
        setInfos(result);
        saveDTO(result.getTouristFoodInfo().getRow());
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
        LocationKeywordSearchForm latLngSearchForm = locationService.getLatLngByAddressRT(row.getADDR(),1,1);
//        LocationKeywordSearchForm latLngSearchForm = locationService.giveLatLngByAddress(row.getADDR());
        if (latLngSearchForm == null) return false;
        LocationKeywordSearchForm.Documents latLngDoc = Arrays.stream(latLngSearchForm.getDocuments()).findFirst().orElse(null);
        if (latLngDoc == null)
            return false;
        row.setLatitude(Decimal.valueOf(latLngDoc.getY()));
        row.setLongitude(Decimal.valueOf(latLngDoc.getX()));
        row.setCategory(categoryFilter(Optional.of(latLngDoc.getCategory_name()).orElse("기타")));
        return true;
    }

    private void setRowCategoryAndId(PublicApiResponseDto.Row row) throws JsonProcessingException {
        LocationKeywordSearchForm categorySearchForm = locationService
                .getCategoryByLatLngKeywordRest(String.valueOf(row.getLatitude()), String.valueOf(row.getLongitude()), row.getSISULNAME());
//        LocationKeywordSearchForm categorySearchForm = locationService.giveCategoryByLatLngKeyword(row.getLatitude(), row.getLongitude(), row.getSISULNAME());
        LocationKeywordSearchForm.Documents categoryDoc = Arrays.stream(categorySearchForm.getDocuments()).findFirst().orElse(null);
        if (categoryDoc == null || !Arrays.asList("FD6", "CE7").contains(categoryDoc.getCategory_group_code()))
            return;
        row.setStoreId(Integer.parseInt(categoryDoc.getId()));
        row.setSISULNAME(categoryDoc.getPlace_name());
        row.setCategory(categoryFilter(Optional.of(categoryDoc.getCategory_name()).orElse(null)));
    }


    private void saveDTO(List<PublicApiResponseDto.Row> rows) {
        List<Store> storeList = rows.stream().filter(PublicApiResponseDto.Row::validation)
                .map(Store::new).collect(Collectors.toList());
        // storeRepository 구현 시 save 호출하기
        for (Store store : storeList) {
            if (!storeRepository.existsById(store.getId())) {
                storeRepository.save(store);
            }
        }
    }

    /**
     * @param category 카테고리가 ,로 구분되어 있는 스트링
     * @return 맞는 카테고리 반환
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
}
