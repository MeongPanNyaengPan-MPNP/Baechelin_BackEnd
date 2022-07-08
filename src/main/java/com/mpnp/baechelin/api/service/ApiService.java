package com.mpnp.baechelin.api.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpnp.baechelin.api.dto.ApiRequestDto;
import com.mpnp.baechelin.api.dto.ApiResponseDto;
import com.mpnp.baechelin.store.domain.Store;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
public class ApiService {
//    /**
//     * @param apiRequestDto : 유저가 등록하는 업소 정보들을 담은 DTO
//     * @return ApiResponseDto - 응답 형태에 맞는 객체 반환
//     * @throws IOException
//     */
//
//    public ApiResponseDto processApiToDBWithWebclient(ApiRequestDto apiRequestDto) throws IOException {
//
//        // 타임아웃 설정
//        HttpClient httpClient = HttpClient.create()
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
//                .responseTimeout(Duration.ofMillis(5000))
//                .doOnConnected(conn ->
//                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
//                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));
//
//        // client 기본설정
//        WebClient client = WebClient.builder()
//                .baseUrl("http://openapi.seoul.go.kr:8088")
////                .defaultCookie("cookieKey", "cookieValue")
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
//                .defaultUriVariables(Collections.singletonMap("url", "http://openapi.seoul.go.kr:8088"))
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .build();
//
//        // 메서드 설정
////        String key = URLEncoder.encode("5274616b45736f7933376e6c525658", "UTF-8"); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
////        String type = URLEncoder.encode("json", "UTF-8"); /*요청파일타입 (xml,xmlf,xls,json) */
////        String service = URLEncoder.encode("touristFoodInfo", "UTF-8"); /*서비스명 (대소문자 구분 필수입니다.)*/
////        String start = URLEncoder.encode("1", "UTF-8"); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
////        String end = URLEncoder.encode("4", "UTF-8"); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/
//
//        String key = URLEncoder.encode(apiRequestDto.getKey(), "UTF-8"); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
//        String type = URLEncoder.encode(apiRequestDto.getType(), "UTF-8"); /*요청파일타입 (xml,xmlf,xls,json) */
//        String service = URLEncoder.encode(apiRequestDto.getService(), "UTF-8"); /*서비스명 (대소문자 구분 필수입니다.)*/
//        String start = URLEncoder.encode(String.valueOf(apiRequestDto.getStartIndex()), "UTF-8"); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
//        String end = URLEncoder.encode(String.valueOf(apiRequestDto.getEndIndex()), "UTF-8"); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(client.get().uri(uriBuilder
//                -> uriBuilder.path("/" + key + "/" + type + "/" + service + "/" + start + "/" + end)
//                .build()).retrieve().bodyToMono(String.class).block());
//
//        return resultMappingToDto(sb.toString());
//
//    }
//    private ApiResponseDto resultMappingToDto(String resultStr){
//        ObjectMapper objectMapper = new ObjectMapper();
//        //private field라서 설정해줘야 한다.
//        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//
//        ApiResponseDto.TouristFoodInfo tfi = null;
//        try {
//            JsonNode jsonNode = objectMapper.readTree(resultStr).get("touristFoodInfo");
//            // list_total_count 생성
//            int list_total_count = Integer.parseInt(String.valueOf(jsonNode.get("list_total_count")));
//            // Result 생성
//            ApiResponseDto.Result result = ApiResponseDto.Result.builder()
//                    .CODE(String.valueOf(jsonNode.get("RESULT").get("CODE")))
//                    .MESSAGE(String.valueOf(jsonNode.get("RESULT").get("MESSAGE")))
//                    .build();
//            // Rows 매핑
//            Iterator<JsonNode> iterator = jsonNode.withArray("row").iterator();
//            List<ApiResponseDto.Row> rows = new ArrayList<>();
//            while (iterator.hasNext()) {
//                JsonNode target = iterator.next();
//                ApiResponseDto.Row row = objectMapper.treeToValue(target, ApiResponseDto.Row.class);
//                rows.add(row);
//            }
//
//            tfi = ApiResponseDto.TouristFoodInfo.builder().list_total_count(list_total_count).RESULT(result).rows(rows).build();
//
//            List<Store> storeList = rows.stream().map(Store::new).collect(Collectors.toList());
//            // storeRepository 구현 시 save 호출하기
////            for(Store store : storeList){
////                storeRepository.save(store);
////            }
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return ApiResponseDto.builder().touristFoodInfo(tfi).build();
//    }
//
//    public ApiResponseDto processApiToDB(ApiRequestDto apiRequestDto) throws IOException {
//        StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088"); /*URL*/
//        urlBuilder.append("/" + URLEncoder.encode("5274616b45736f7933376e6c525658", "UTF-8")); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
//        urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8")); /*요청파일타입 (xml,xmlf,xls,json) */
//        urlBuilder.append("/" + URLEncoder.encode("touristFoodInfo", "UTF-8")); /*서비스명 (대소문자 구분 필수입니다.)*/
//        urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8")); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
//        urlBuilder.append("/" + URLEncoder.encode("5", "UTF-8")); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/
//        // 상위 5개는 필수적으로 순서바꾸지 않고 호출해야 합니다.
//
//        // 서비스별 추가 요청 인자이며 자세한 내용은 각 서비스별 '요청인자'부분에 자세히 나와 있습니다.
//
//        URL url = new URL(urlBuilder.toString());
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/xml");
//        System.out.println("Response code: " + conn.getResponseCode()); /* 연결 자체에 대한 확인이 필요하므로 추가합니다.*/
//        BufferedReader rd;
//
//        // 서비스코드가 정상이면 200~300사이의 숫자가 나옵니다.
//        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        } else {
//            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//        }
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = rd.readLine()) != null) {
//            sb.append(line);
//        }
//        rd.close();
//        conn.disconnect();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        //private field라서 설정해줘야 한다.
//        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//
//        ApiResponseDto.TouristFoodInfo tfi = null;
//        try {
//            JsonNode jsonNode = objectMapper.readTree(sb.toString()).get("touristFoodInfo");
//            // list_total_count 생성
//            int list_total_count = Integer.parseInt(String.valueOf(jsonNode.get("list_total_count")));
//            // Result 생성
//            ApiResponseDto.Result result = ApiResponseDto.Result.builder()
//                    .CODE(String.valueOf(jsonNode.get("RESULT").get("CODE")))
//                    .MESSAGE(String.valueOf(jsonNode.get("RESULT").get("MESSAGE")))
//                    .build();
//            // Rows 매핑
//            Iterator<JsonNode> iterator = jsonNode.withArray("row").iterator();
//            List<ApiResponseDto.Row> rows = new ArrayList<>();
//            while (iterator.hasNext()) {
//                JsonNode target = iterator.next();
//                ApiResponseDto.Row row = objectMapper.treeToValue(target, ApiResponseDto.Row.class);
//                rows.add(row);
//            }
//
//            tfi = ApiResponseDto.TouristFoodInfo.builder().list_total_count(list_total_count).RESULT(result).rows(rows).build();
//
//            List<Store> storeList = rows.stream().map(Store::new).collect(Collectors.toList());
//            // storeRepository 구현 시 save 호출하기
////            for(Store store : storeList){
////                storeRepository.save(store);
////            }
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return ApiResponseDto.builder().touristFoodInfo(tfi).build();
//    }
}
