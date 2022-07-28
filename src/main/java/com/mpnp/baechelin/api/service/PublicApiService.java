package com.mpnp.baechelin.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mpnp.baechelin.api.dto.*;
import com.mpnp.baechelin.api.model.PublicApiV1Form;
import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.store.domain.Category;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.store.service.StoreImageService;
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
@Slf4j
@RequiredArgsConstructor
public class PublicApiService {
    private final StoreRepository storeRepository;
    private final LocationService locationService;
    private final StoreImageService storeImageService;
    @Value("${public.api.v1.key}")
    private String publicV1Key;

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
        LocationPartDto.LatLong latLong = locationService.convertAddressToGeo(row.getADDR());
        if (latLong == null || !latLong.validate()) return false;
        row.setLatitude(Decimal.valueOf(latLong.getLatitude()));
        row.setLongitude(Decimal.valueOf(latLong.getLongitude()));
        return true;
    }

    /**
     * @param row 행 하나하나
     * @throws JsonProcessingException JSON 파싱, 매핑 오류시 발생하는 Exception
     */
    private void setRowCategoryAndId(PublicApiV1Form.Row row) throws JsonProcessingException {
        LocationInfoDto.LocationResponse locationResponse = locationService
                .convertGeoAndStoreNameToKeyword(String.valueOf(row.getLatitude()), String.valueOf(row.getLongitude()), row.getSISULNAME());
        if (locationResponse == null)
            return; // 결과가 비어있으면 진행하지 않는다
        row.setStoreId(locationResponse.getStoreId());
        row.setSISULNAME(locationResponse.getStoreName());
        row.setCategory(locationResponse.getCategory());
    }

    /**
     * @param rows 검증할 행
     */
    @Transactional
    public void saveValidStores(List<PublicApiV1Form.Row> rows) {
        List<Store> storeList = rows.stream().filter(PublicApiV1Form.Row::validation)
                .map(Store::new).collect(Collectors.toList());
        // storeRepository 구현 시 save 호출하기
        for (Store store : storeList) {
            if (!storeRepository.existsById(store.getId())) {
                storeRepository.saveAndFlush(store);
                storeImageService.saveImage(store.getId());
            }
        }
    }
}
