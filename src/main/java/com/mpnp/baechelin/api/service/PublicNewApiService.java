package com.mpnp.baechelin.api.service;

import com.mpnp.baechelin.api.dto.PublicApiRequestDto;
import com.mpnp.baechelin.api.dto.PublicApiResponseDto;
import com.mpnp.baechelin.api.model.PublicApiCategoryForm;
import com.mpnp.baechelin.api.model.PublicApiForm;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.service.StoreService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class PublicNewApiService {
    private final StoreService storeService;

    public PublicApiForm processNewApi(String key, int requestSize, String siDoNm) {
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
        if (result == null) return null;
        List<Store> storeList = new ArrayList<>();
        for (PublicApiForm.ServList servList : result.getServList()) {
            // servList + Barrier Free Tag 합치기
            List<String> barrierTagList = processNewApiSecondStage(key, servList.getWfcltId());
            log.info("barrierlist : {}", barrierTagList.toString());
//            storeService.addNewPublicApiObj(servList, barrierTagList);
        }
        return result;
    }

    public List<String> processNewApiSecondStage(String key, String sisulNum) {
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
    @Getter
    protected enum BarrierCode {
        ELEVATOR("계단 또는 승강설비", "elevator"),
        TOILET_A("소변기", "toilet"),
        TOILET_B("대변기", "toilet"),
        PARKING("장애인전용주차구역", "parking"),
        HEIGHT_DIFFERENCE("주출입구 높이차이 제거", "height_different"),
        APPROACH("주출입구 접근로", "approach"),
        ETC("", null);
        private final String desc;
        private final String columnName;

        BarrierCode(String desc, String columnName) {
            this.desc = desc;
            this.columnName = columnName;
        }

        private static String getColumnFromDesc(String desc) {
            BarrierCode barrierCode = Arrays.stream(BarrierCode.values())
                    .filter(b -> b.getDesc().equals(desc)).findFirst().orElse(ETC);
            return barrierCode.getColumnName();
        }

    }

}
