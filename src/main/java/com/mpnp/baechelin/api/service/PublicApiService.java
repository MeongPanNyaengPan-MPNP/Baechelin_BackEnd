package com.mpnp.baechelin.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mpnp.baechelin.api.dto.*;
import com.mpnp.baechelin.api.model.LocationKeywordSearchForm;
import com.mpnp.baechelin.api.model.PublicApiV1Form;
import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.store.domain.Category;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.ion.Decimal;

import javax.transaction.Transactional;
import java.net.URI;
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
    /*private final HttpConfig httpConfig;

    public PublicApiResponseDto processApiToDBWithWebclientMono(PublicApiRequestDto publicApiRequestDto) throws UnsupportedEncodingException {
        WebClient client = WebClient.builder()
                .baseUrl("http://openapi.seoul.go.kr:8088")
//                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://openapi.seoul.go.kr:8088"))
                .clientConnector(new ReactorClientHttpConnector(httpConfig.httpClient())) // 위의 타임아웃 적용
                .build();

        String key = URLEncoder.encode(publicV1Key, "UTF-8"); *//*인증키 (sample사용시에는 호출시 제한됩니다.)*//*
        String type = URLEncoder.encode(publicApiRequestDto.getType(), "UTF-8"); *//*요청파일타입 (xml,xmlf,xls,json) *//*
        String service = URLEncoder.encode(publicApiRequestDto.getService(), "UTF-8"); *//*서비스명 (대소문자 구분 필수입니다.)*//*
        String start = URLEncoder.encode(String.valueOf(publicApiRequestDto.getStartIndex()), "UTF-8"); *//*요청시작위치 (sample인증키 사용시 5이내 숫자)*//*
        String end = URLEncoder.encode(String.valueOf(publicApiRequestDto.getEndIndex()), "UTF-8"); *//*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*//*

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

    }*/

    /**
     * @param publicApiRequestDto Controller에서 받은 DTO(key 등이 포함됨)
     */
    public void processApiV1(PublicApiRequestDto publicApiRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        URI uri = UriComponentsBuilder
                .fromUriString("http://openapi.seoul.go.kr:8088")
                .path("/{key}/{type}/{service}/{start}/{end}")
                .buildAndExpand(publicV1Key, publicApiRequestDto.getType(),
                        publicApiRequestDto.getService(), publicApiRequestDto.getStartIndex(),
                        publicApiRequestDto.getEndIndex())
                .encode()
                .toUri();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PublicApiV1Form> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), PublicApiV1Form.class
        );
        PublicApiV1Form result = resultRe.getBody();
        if (result == null) {
            return;
        }
        setInfos(result);
        saveValidStores(result.getTouristFoodInfo().getRow());
    }

    /**
     * @param publicApiV1Form API 호출 결과
     *
     */
    private void setInfos(PublicApiV1Form publicApiV1Form) {
        publicApiV1Form.getTouristFoodInfo().getRow().forEach(row -> {
                try {
                    if (!setRowLngLat(row)) return; // 주소를 가지고 위/경도를 찾는다
                } catch (JsonProcessingException e) {
                    throw new CustomException(ErrorCode.API_LOAD_FAILURE);
                }
                try {
                    setRowCategoryAndId(row); // 위/경도/매장명을 가지고 키워드 설정
                } catch (JsonProcessingException e) {
                    throw new CustomException(ErrorCode.API_LOAD_FAILURE);
                }
            }
        );
    }


    /**
     * @param row 공공 API 결과에서의 각각의 행
     * @return 위도, 경도 매핑 성공/실패 여부
     * @throws JsonProcessingException JSON 파싱, 매핑 오류시 발생하는 Exception
     */
    private boolean setRowLngLat(PublicApiV1Form.Row row) throws JsonProcessingException {
        LocationKeywordSearchForm latLngSearchForm = locationService.getLatLngByAddressRT(row.getADDR());
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

    /**
     * @param row 행 하나하나
     * @throws JsonProcessingException JSON 파싱, 매핑 오류시 발생하는 Exception
     */
    private void setRowCategoryAndId(PublicApiV1Form.Row row) throws JsonProcessingException {
        LocationKeywordSearchForm categorySearchForm = locationService
                .getCategoryByLatLngKeywordRT(String.valueOf(row.getLatitude()), String.valueOf(row.getLongitude()), row.getSISULNAME());
//        LocationKeywordSearchForm categorySearchForm = locationService.giveCategoryByLatLngKeyword(row.getLatitude(), row.getLongitude(), row.getSISULNAME());
        LocationKeywordSearchForm.Documents categoryDoc = Arrays.stream(categorySearchForm.getDocuments()).findFirst().orElse(null);
        if (categoryDoc == null)
            return; // 결과가 비어있으면 진행하지 않는다
        row.setStoreId(Integer.parseInt(categoryDoc.getId()));
        row.setSISULNAME(categoryDoc.getPlace_name());
        row.setCategory(categoryFilter(Optional.of(categoryDoc.getCategory_name()).orElse(null)));
    }

    /**
     * @param rows 검증할 행
     */
    private void saveValidStores(List<PublicApiV1Form.Row> rows) {
        List<Store> storeList = rows.stream().filter(PublicApiV1Form.Row::validation)
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
