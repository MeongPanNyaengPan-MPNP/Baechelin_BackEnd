package com.mpnp.baechelin.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mpnp.baechelin.api.model.BarrierCode;
import com.mpnp.baechelin.api.model.PublicApiCategoryForm;
import com.mpnp.baechelin.api.model.PublicApiForm;
import com.mpnp.baechelin.api.dto.*;
import com.mpnp.baechelin.api.model.LocationKeywordSearchForm;
import com.mpnp.baechelin.store.domain.Category;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.ion.Decimal;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
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
    private final StoreService storeService;

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
//        String key = URLEncoder.encode(publicApiRequestDto.getKey(), "UTF-8"); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
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

    public PublicApiResponseDto processApiToDBWithRestTemplate(PublicApiRequestDto publicApiRequestDto) {
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
        row.setLatitude(Decimal.valueOf(latLngDoc.getY()));
        row.setLongitude(Decimal.valueOf(latLngDoc.getX()));
        // 카테고리 ENUM으로 전환하기
        row.setCategory(categoryFilter(Optional.of(latLngDoc.getCategory_name()).orElse("기타")));
        return true;
    }

    private void setRowCategoryAndId(PublicApiResponseDto.Row row) throws JsonProcessingException {
        LocationKeywordSearchForm categorySearchForm = locationService
                .giveCategoryByLatLngKeywordRest(String.valueOf(row.getLatitude()), String.valueOf(row.getLongitude()), row.getSISULNAME());
//        LocationKeywordSearchForm categorySearchForm = locationService.giveCategoryByLatLngKeyword(row.getLatitude(), row.getLongitude(), row.getSISULNAME());
        LocationKeywordSearchForm.Documents categoryDoc = Arrays.stream(categorySearchForm.getDocuments()).findFirst().orElse(null);
        if (categoryDoc == null || !Arrays.asList("FD6", "CE7").contains(categoryDoc.getCategory_group_code()))
            return;
        row.setStoreId(Integer.parseInt(categoryDoc.getId()));
        row.setSISULNAME(categoryDoc.getPlace_name());
        row.setCategory(categoryFilter(Optional.of(categoryDoc.getCategory_name()).orElse(null)));
    }


    private void saveDTO(List<PublicApiResponseDto.Row> rows) {
        List<Store> storeList = rows.stream().filter(this::publicRowValidation)
                .map(Store::new).collect(Collectors.toList());
        // storeRepository 구현 시 save 호출하기
        for (Store store : storeList) {
            if (!storeRepository.existsById(store.getId())) {
                storeRepository.save(store);
//            } else {
//                storeRepository.findById(store.getId())
//                        .ifPresent(s -> {
//                            s.setCategory(store.getCategory());
//                            storeRepository.save(s);
//                        });
//
//            }
            }
        }
    }

    public List<StoreCardResponseDto> processNewApi(String key, int requestSize, String siDoNm) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(List.of(MediaType.APPLICATION_XML));
        URI uri = UriComponentsBuilder
                .fromUriString("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getDisConvFaclList")
                .queryParam("serviceKey", key)
                .queryParam("numOfRows", requestSize)
                .queryParam("siDoNm", siDoNm)
                .queryParam("faclTyCd", "UC0B01")
                .build()
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        log.warn(uri.toString());
        ResponseEntity<PublicApiForm> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), PublicApiForm.class
        );
        PublicApiForm result = resultRe.getBody();
        return getStoreCardResponseDto(key, result);
    }

    private List<StoreCardResponseDto> getStoreCardResponseDto(String key, PublicApiForm result) {
        if (result == null || result.getServList() == null) return null;
        List<Store> storeList = new ArrayList<>();
        for (PublicApiForm.ServList servList : result.getServList()) {
            // servList + Barrier Free Tag 합치기 + category
            if (!servList.validateServList()) continue;

            List<String> barrierTagList = tagStringToList(key, servList.getWfcltId());
            if (barrierTagList.isEmpty()) continue;

            log.info("barrierlist : {}", barrierTagList);

            Map<String, Object> infoMap
                    = locationService.convertGeoAndStoreNameToKeyword(servList.getFaclLat(), servList.getFaclLng(), servList.getFaclNm());

            if ((boolean) infoMap.get("status")) {
                int storeId = (Integer) infoMap.get("storeId");
                String category = (String) infoMap.get("category");
                String phoneNumber = (String) infoMap.get("phoneNumber");
                String storeName = (String) infoMap.get("storeName");
                Store nStore = new Store(storeId, servList, barrierTagList, phoneNumber, category, storeName);
                if (!storeRepository.existsById(nStore.getId())) {
                    storeRepository.save(nStore);
                    storeList.add(nStore);
                }
            }
        }
        return storeList.stream().map(StoreCardResponseDto::new).collect(Collectors.toList());
    }

    public List<String> tagStringToList(String key, String sisulNum) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(List.of(MediaType.APPLICATION_XML));
        URI uri = UriComponentsBuilder
                .fromUriString("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getFacInfoOpenApiJpEvalInfoList")
                .queryParam("serviceKey", key)
                .queryParam("wfcltId", sisulNum)
                .build()
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        log.warn(uri.toString());
        ResponseEntity<PublicApiCategoryForm> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), PublicApiCategoryForm.class
        );
        PublicApiCategoryForm result = resultRe.getBody();
        return tagMapping(result);
    }

    private List<String> tagMapping(PublicApiCategoryForm result) {
        List<String> barrierTagResult = new ArrayList<>(); // 태그 결과들을 담을 리스트
        if (result == null || result.getServList() == null) {
            return barrierTagResult;
        } else {
            PublicApiCategoryForm.ServList first = result.getServList().stream().findFirst().orElse(null);
            // Input 한 개당 하나의 배리어 프리 정보가 생성되므로 하나만 찾는다
            if (first != null && first.getEvalInfo() != null) { // 결과가 존재할 떄
                String[] splitInput = first.getEvalInfo().split(",");
                return Arrays.stream(splitInput)
                        .map(BarrierCode::getColumnFromDesc)
                        .filter(code -> code != null && !code.equals(""))
                        .collect(Collectors.toList());
            }
        }
        return barrierTagResult;
    }

    /*
     *계단 또는 승강설비,대변기,복도,소변기,일반사항,장애인전용주차구역,주출입구 높이차이 제거,주출입구 접근로,출입구(문),해당시설 층수
     *  */


    private String categoryFilter(String category) {
        if (category == null) {
            return Category.ETC.getDesc();
        } else if (category.contains(">")) {
            return Category.giveCategory(category.split(" > ")[1]).getDesc();
        } else {
            return null;
        }
    }

    private boolean publicRowValidation(PublicApiResponseDto.Row row) {
        return row.getLatitude() != null && row.getLongitude() != null && row.getCategory() != null && row.getStoreId() != null;
    }

}
