package com.mpnp.baechelin.api.service;

import com.mpnp.baechelin.api.model.BarrierCode;
import com.mpnp.baechelin.api.model.PublicApiCategoryForm;
import com.mpnp.baechelin.api.model.PublicApiForm;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.dto.StoreCardResponseDto;
import com.mpnp.baechelin.store.repository.StoreRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
/**
 * 공공 API V2 서비스 - V1과의 분리를 위해
 */
public class PublicApiServiceV2 {
    private final StoreRepository storeRepository;
    private final LocationService locationService;

    public PublicApiServiceV2(StoreRepository storeRepository, LocationService locationService) {
        this.storeRepository = storeRepository;
        this.locationService = locationService;
    }

    @Value("${public.api.v2.key}")
    private String publicV2Key;

    private final String publicV2Uri = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getDisConvFaclList";
    private final String publicV2CategoryUri = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getFacInfoOpenApiJpEvalInfoList";

    /**
     * @param siDoNm 찾고자 하는 시
     * @return 총 데이터 개수
     */
    private int getPageSize(String siDoNm, String cggNm) {
        // 헤더 세팅
        HttpHeaders headers = setHttpHeaders();
        // 파라미터 설정
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("serviceKey", publicV2Key);
        paramMap.put("siDoNm", siDoNm);
        paramMap.put("cggNm", cggNm);
        paramMap.put("numOfRows", "500");
        paramMap.put("faclTyCd", "UC0B01");
        // URI 생성
        URI uri = createUriRT(publicV2Uri, paramMap);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PublicApiForm> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), PublicApiForm.class
        );
        PublicApiForm result = resultRe.getBody();
        if (result == null)
            return 0;
        else
            return result.getTotalCount();
    }

    /**
     * @param totalDataCount 시 & 구에 해당하는 데이터 개수
     * @param nowPage        현재 페이지
     * @return 다음 페이지가 존재하는지 - 페이지는 1000으로 고정
     */
    private boolean hasNextPage(int totalDataCount, int nowPage) {
        if (totalDataCount <= 1000) return false;
        return totalDataCount > nowPage * 1000;
    }

    /**
     * @return 헤더 세팅 - V2에서는 공통으로 XML 사용
     */
    private HttpHeaders setHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(List.of(MediaType.APPLICATION_XML));
        return headers;
    }

    /**
     * @param uriStr   URI 주소
     * @param paramMap 파라미터가 들어있는 Map
     * @return URI와 파라미터를 결합한 최종 주소
     */
    public URI createUriRT(String uriStr, Map<String, String> paramMap) {
        return UriComponentsBuilder
                .fromUriString(uriStr).buildAndExpand(paramMap)
                .encode().toUri();
    }

    /**
     * @param siDoNm 데이터를 가져올 시
     */
    public boolean processApi(String siDoNm, String cggNm, int pageNo) {
        // 헤더 세팅
        HttpHeaders headers = setHttpHeaders();
        // 파라미터 설정
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("serviceKey", publicV2Key);
        paramMap.put("numOfRows", "1000");
        paramMap.put("pageNo", String.valueOf(pageNo));
        paramMap.put("siDoNm", siDoNm);
        paramMap.put("cggNm", cggNm);
        paramMap.put("faclTyCd", "UC0B01");
        // URI 생성
        URI uri = createUriRT(publicV2Uri, paramMap);

        RestTemplate restTemplate = new RestTemplate();
        log.warn(uri.toString());
        ResponseEntity<PublicApiForm> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), PublicApiForm.class
        );
        PublicApiForm result = resultRe.getBody();
        if (result == null)
            return false;
        int totalSize = result.getTotalCount();
        getStoreCardResponseDto(result);
        return hasNextPage(totalSize, pageNo);
    }

    private List<StoreCardResponseDto> getStoreCardResponseDto(PublicApiForm result) {
        if (result == null || result.getServList() == null) return null;
        List<Store> storeList = new ArrayList<>();
        for (PublicApiForm.ServList servList : result.getServList()) {
            // servList + Barrier Free Tag 합치기 + category
            if (!servList.validateServList()) continue;

            List<String> barrierTagList = tagStrToList(servList.getWfcltId());
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

    public List<String> tagStrToList(String sisulNum) {
        HttpHeaders headers = setHttpHeaders();
        URI uri = UriComponentsBuilder
                .fromUriString("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getFacInfoOpenApiJpEvalInfoList")
                .queryParam("serviceKey", publicV2Key)
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
            if (first != null && first.validation()) { // 결과가 존재할 떄
                String[] splitInput = first.getEvalInfo().split(",");
                return Arrays.stream(splitInput)
                        .map(BarrierCode::getColumnFromDesc)
                        .filter(code -> code != null && !code.equals(""))
                        .collect(Collectors.toList());
            }
        }
        return barrierTagResult;
    }
}
