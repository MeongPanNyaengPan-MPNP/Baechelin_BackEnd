package com.mpnp.baechelin.map.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/map")
public class MapController {
    @GetMapping
    public String callMap(@RequestParam Integer latitude, @RequestParam Integer longitude) {
        return "check";
    }

    /**
     * @param storeInfo : 검색에 이용할 키워드 - 주소와 업장명을 합치면 범위가 좁아져 조회가 쉬워질 것 같습니다!
     * @return API 호출 결과값 - API 호출 결과 중 카테고리를 리턴
     */
    @GetMapping("/store")
    public String checkStoreTag(@RequestParam String storeInfo) {
//        public JsonNode checkStoreTag(@RequestParam String storeInfo) {
        String clientId = "fJLCJG1qPVhGJcOgFOfi"; //애플리케이션 클라이언트 아이디값"
        String clientSecret = "ViVTM2AHTl"; //애플리케이션 클라이언트 시크릿값"

        String text = null;
        try {
            text = URLEncoder.encode(storeInfo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/local?query=" + text;    // json 결과

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL, requestHeaders);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        String category = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
            Iterator<JsonNode> iterator = jsonNode.iterator();
            category = jsonNode.get("items").get(0).get("category").asText().split(">")[0];
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //return responseBody;
        return category;

//        return jsonNode;
    }

    private String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);
        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
