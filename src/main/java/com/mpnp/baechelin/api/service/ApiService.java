package com.mpnp.baechelin.api.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpnp.baechelin.api.config.HttpConfig;
import com.mpnp.baechelin.api.dto.*;
import com.mpnp.baechelin.map.service.MapService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
public class ApiService {
    /**
     * @param apiRequestDto : 유저가 등록하는 업소 정보들을 담은 DTO
     * @return ApiResponseDto - 응답 형태에 맞는 객체 반환
     * @throws IOException
     */
    // store repo 구현 시 쓸 예정!
    private final StoreRepository storeRepository;
    private final MapService mapService;
    private final HttpConfig httpConfig;
    ObjectMapper objectMapper = new ObjectMapper();

    public ApiResponseDto processApiToDBWithWebclientMono(ApiRequestDto apiRequestDto) throws UnsupportedEncodingException {
        WebClient client = WebClient.builder()
                .baseUrl("http://openapi.seoul.go.kr:8088")
//                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://openapi.seoul.go.kr:8088"))
                .clientConnector(new ReactorClientHttpConnector(httpConfig.httpConfig())) // 위의 타임아웃 적용
                .build();

        String key = URLEncoder.encode(apiRequestDto.getKey(), "UTF-8"); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
        String type = URLEncoder.encode(apiRequestDto.getType(), "UTF-8"); /*요청파일타입 (xml,xmlf,xls,json) */
        String service = URLEncoder.encode(apiRequestDto.getService(), "UTF-8"); /*서비스명 (대소문자 구분 필수입니다.)*/
        String start = URLEncoder.encode(String.valueOf(apiRequestDto.getStartIndex()), "UTF-8"); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
        String end = URLEncoder.encode(String.valueOf(apiRequestDto.getEndIndex()), "UTF-8"); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/
        return client.get().uri(
                        uriBuilder
                                -> uriBuilder.pathSegment(key, type, service, start, end).path("/")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {throw new IllegalAccessError("400");})
                .onStatus(HttpStatus::is5xxServerError, response ->  {throw new IllegalAccessError("500");})
                .bodyToMono(ApiResponseDto.class).block();

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
    private ApiResponseDto resultMappingToDto(String resultStr) {

        //private field라서 설정해줘야 한다.
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        ApiResponseDto.TouristFoodInfo touristFoodInfo = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(resultStr).get("touristFoodInfo");
            // list_total_count 생성
            int list_total_count = Integer.parseInt(jsonNode.get("list_total_count").asText());
            // Result 생성
            ApiResponseDto.Result result = ApiResponseDto.Result.builder()
                    .CODE(jsonNode.get("RESULT").get("CODE").asText())
                    .MESSAGE(jsonNode.get("RESULT").get("MESSAGE").asText())
                    .build();

            // Rows 매핑
            Iterator<JsonNode> iterator = jsonNode.withArray("row").iterator();
            List<ApiResponseDto.Row> rows = new ArrayList<>();
            while (iterator.hasNext()) {
                JsonNode target = iterator.next();
                ApiResponseDto.Row row = objectMapper.treeToValue(target, ApiResponseDto.Row.class);
                Map<String, Object> infos = mapService.giveInfoByKeyword(row.getADDR());

                // 값을 찾았다면 ( Map 내의 "message"가 true 일 경우 )
                if ((Boolean) infos.get("message")) {
                    row.setLatitude(infos.get("latitude").toString());
                    row.setLongitude(infos.get("longitude").toString());
                    row.setCategory(infos.get("category").toString());
                    rows.add(row);
                }  // TODO 주소로 값이 조회되지 않을 때 - 버릴 것인가 생각해 보기
            }

            touristFoodInfo = ApiResponseDto.TouristFoodInfo.builder()
                    .list_total_count(list_total_count)
                    .RESULT(result)
                    .row(rows)
                    .build();

            saveDtos(rows);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ApiResponseDto.builder().touristFoodInfo(touristFoodInfo).build();
    }

    private void saveDtos(List<ApiResponseDto.Row> rows) {
        List<Store> storeList = rows.stream().map(Store::new).collect(Collectors.toList());
        // storeRepository 구현 시 save 호출하기
        storeRepository.saveAll(storeList);
    }

    /**
     * 쓰지는 않지만 java로 구현한 코드를 Ref로 삼습니다
     */
    public ApiResponseDto processApiToDB(ApiRequestDto apiRequestDto) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088"); /*URL*/
        urlBuilder.append("/" + URLEncoder.encode("5274616b45736f7933376e6c525658", "UTF-8")); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
        urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8")); /*요청파일타입 (xml,xmlf,xls,json) */
        urlBuilder.append("/" + URLEncoder.encode("touristFoodInfo", "UTF-8")); /*서비스명 (대소문자 구분 필수입니다.)*/
        urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8")); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
        urlBuilder.append("/" + URLEncoder.encode("5", "UTF-8")); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/
        // 상위 5개는 필수적으로 순서바꾸지 않고 호출해야 합니다.

        // 서비스별 추가 요청 인자이며 자세한 내용은 각 서비스별 '요청인자'부분에 자세히 나와 있습니다.

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/xml");
        System.out.println("Response code: " + conn.getResponseCode()); /* 연결 자체에 대한 확인이 필요하므로 추가합니다.*/
        BufferedReader rd;

        // 서비스코드가 정상이면 200~300사이의 숫자가 나옵니다.
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        return resultMappingToDto(sb.toString());
    }
}
