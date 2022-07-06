package com.mpnp.baechelin.api.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpnp.baechelin.api.config.HttpConfig;
import com.mpnp.baechelin.api.dto.*;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
                .clientConnector(new ReactorClientHttpConnector(httpConfig.httpConfig())) // 위의 타임아웃 적용
                .build();

        String key = URLEncoder.encode(publicApiRequestDto.getKey(), "UTF-8"); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
        String type = URLEncoder.encode(publicApiRequestDto.getType(), "UTF-8"); /*요청파일타입 (xml,xmlf,xls,json) */
        String service = URLEncoder.encode(publicApiRequestDto.getService(), "UTF-8"); /*서비스명 (대소문자 구분 필수입니다.)*/
        String start = URLEncoder.encode(String.valueOf(publicApiRequestDto.getStartIndex()), "UTF-8"); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
        String end = URLEncoder.encode(String.valueOf(publicApiRequestDto.getEndIndex()), "UTF-8"); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/
        return client.get().uri(
                        uriBuilder
                                -> uriBuilder.pathSegment(key, type, service, start, end).path("/")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {throw new IllegalAccessError("400");})
                .onStatus(HttpStatus::is5xxServerError, response ->  {throw new IllegalAccessError("500");})
                .bodyToMono(PublicApiResponseDto.class).block();

        /*StringBuffer buf = new StringBuffer();
        buf.append(client.get().uri(
                        uriBuilder
                                -> uriBuilder.pathSegment(key, type, service, start, end).path("/")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ApiResponseDto.class).block());

        return resultMappingToDto(buf.toString());*/
    }


    /**
     * @Param String resultStr :
     * @Return
     */
    private PublicApiResponseDto resultMappingToDto(String resultStr) {

        //private field라서 설정해줘야 한다.
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        PublicApiResponseDto.TouristFoodInfo touristFoodInfo = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(resultStr).get("touristFoodInfo");
            // list_total_count 생성
            int list_total_count = Integer.parseInt(jsonNode.get("list_total_count").asText());
            // Result 생성
            PublicApiResponseDto.Result result = PublicApiResponseDto.Result.builder()
                    .CODE(jsonNode.get("RESULT").get("CODE").asText())
                    .MESSAGE(jsonNode.get("RESULT").get("MESSAGE").asText())
                    .build();

            // Rows 매핑
            Iterator<JsonNode> iterator = jsonNode.withArray("row").iterator();
            List<PublicApiResponseDto.Row> rows = new ArrayList<>();
            while (iterator.hasNext()) {
                JsonNode target = iterator.next();
                PublicApiResponseDto.Row row = objectMapper.treeToValue(target, PublicApiResponseDto.Row.class);
                Map<String, Object> infos = locationService.giveInfoByKeyword(row.getADDR());

                // 값을 찾았다면 ( Map 내의 "message"가 true 일 경우 )
                if ((Boolean) infos.get("message")) {
                    row.setLatitude(infos.get("latitude").toString());
                    row.setLongitude(infos.get("longitude").toString());
                    row.setCategory(infos.get("category").toString());
                    rows.add(row);
                }  // TODO 주소로 값이 조회되지 않을 때 - 버릴 것인가 생각해 보기
            }

            touristFoodInfo = PublicApiResponseDto.TouristFoodInfo.builder()
                    .list_total_count(list_total_count)
                    .RESULT(result)
                    .row(rows)
                    .build();

            saveDtos(rows);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return PublicApiResponseDto.builder().touristFoodInfo(touristFoodInfo).build();
    }

    private void saveDtos(List<PublicApiResponseDto.Row> rows) {
        List<Store> storeList = rows.stream().map(Store::new).collect(Collectors.toList());
        // storeRepository 구현 시 save 호출하기
        storeRepository.saveAll(storeList);
    }

}
