package com.mpnp.baechelin.api.service;

import com.mpnp.baechelin.api.model.BarrierCode;
import com.mpnp.baechelin.api.model.PublicApiCategoryForm;
import com.mpnp.baechelin.api.model.PublicApiV2Form;
import com.mpnp.baechelin.common.DataClarification;
import com.mpnp.baechelin.config.AppConfig;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.api.dto.LocationInfoDto;
import com.mpnp.baechelin.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
/**
 * 공공 API V2 서비스 - V1과의 분리를 위해
 */
public class PublicApiServiceV2 {
    private final StoreRepository storeRepository;
    private final LocationService locationService;

    @Value("${public.api.v2.key}")
    private String publicV2Key;


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
     * @param siDoNm 데이터를 가져올 시(지역)
     * @param cggNm  데이터를 가져올 구(지역)
     * @param pageNo 데이터 가져올 페이지
     */
    public void processApi(String siDoNm, String cggNm, int pageNo) {
        // 헤더 세팅
        HttpHeaders headers = setHttpHeaders();
        log.info("{}, {}, print", siDoNm, cggNm);
        // URI 생성
        String publicV2Uri = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getDisConvFaclList";
        URI uri = UriComponentsBuilder
                .fromUriString(publicV2Uri)
                .queryParam("serviceKey", publicV2Key)
                .queryParam("numOfRows", "1000")
                .queryParam("pageNo", String.valueOf(pageNo))
                .queryParam("siDoNm", siDoNm)
                .queryParam("cggNm", cggNm)
                .queryParam("faclTyCd", "UC0B01")
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        log.warn(uri.toString());
        ResponseEntity<PublicApiV2Form> resultRe = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), PublicApiV2Form.class
        );
        PublicApiV2Form result = resultRe.getBody();
        if (result == null) // 결과가 없으면 false 리턴
            return;
        processForm(result);
        // totalSize, 현 페이지를 통해 다음 페이지가 있는지 확인하고 T/F 리턴
    }

    /**
     * @param formResult 공공 API 결과에서 각각의 Row
     */
    private void processForm(PublicApiV2Form formResult) {
        if (formResult == null || formResult.getServList() == null) return;
        // servList + Barrier Free Tag  + category
        for (PublicApiV2Form.ServList servList : formResult.getServList()) {
            // servList 요소 - 각각의 배리어 프리 업장 하나하나를 검증
            if (!servList.validateServList()) continue;
            mapApiToStoreWithPaging(servList);
        }
        // 검증 완료된 store들을 저장
    }

    /**
     * @param servList V2의 결과 Row
     */
    private void mapApiToStoreWithPaging(PublicApiV2Form.ServList servList) {
        // 태그 String을 분리 & 매핑해 리스트에 저장
        List<String> barrierTagList = tagStrToList(servList.getWfcltId());

        // TODO 태그가 비어있다면 어떻게 해야 할 지 ? -> 저장 혹은 버리기 (현재 버리기로 구현)
        if (barrierTagList.isEmpty()) return;

        // 위도, 경도, 업장 이름을 통해 업장 정보를 얻어온다
        /*
         * Strategy = 업장명 + 위/경도를 사용해 Store가 존재한다면, 하나만 저장
         * 존재하지 않는다면, 주소 + 위/경도를 사용해 해당 건물의 배리어 프리 매장들을
         * 등록하도록 변경             */
        if (searchWithStoreName(servList, barrierTagList)) return;
        // 검색 결과가 없을 경우
        searchWithAddress(servList, barrierTagList);
    }

    /**
     * @param servList       대상 Row
     * @param barrierTagList 배리어 태그 리스트
     * @return 검색 결과 존재 여부
     */
    private boolean searchWithStoreName(PublicApiV2Form.ServList servList, List<String> barrierTagList) {
        LocationInfoDto.LocationResponse resultDto =
                locationService.convertGeoAndStoreNameToKeyword(servList.getFaclLat(), servList.getFaclLng(), servList.getFaclNm());
        if (resultDto == null)
            return false;
        Store nStore = new Store(resultDto, servList, barrierTagList);
        if (!storeRepository.existsById(nStore.getId()))
            storeRepository.save(nStore);
        return true;
    }

    /**
     * @param servList       대상 Row
     * @param barrierTagList 배리어 태그 리스트
     */
    private void searchWithAddress(PublicApiV2Form.ServList servList, List<String> barrierTagList) {
        List<LocationInfoDto.LocationResponse> locationResponseMapList = locationService
                .convertGeoAndAddressToKeyword(servList.getFaclLat(), servList.getFaclLng(), DataClarification.clarifyString(servList.getLcMnad()));

        for (LocationInfoDto.LocationResponse locationResponse : locationResponseMapList) {
            Store nStore = new Store(locationResponse, servList, barrierTagList);

            // ID 값으로 store 중복 검사해 중복되지 않을 시에만 리스트에 저장
            if (!storeRepository.existsById(nStore.getId())) {
                storeRepository.save(nStore);
            }
        }
    }

    /**
     * @param sisulNum 시설 고유 번호
     * @return API 결과로 나온 문자열을 리스트로 분리
     */
    public List<String> tagStrToList(String sisulNum) {
        HttpHeaders headers = setHttpHeaders();
        String publicV2CategoryUri = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getFacInfoOpenApiJpEvalInfoList";
        URI uri = UriComponentsBuilder
                .fromUriString(publicV2CategoryUri)
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
        return mapTags(result);
    }

    /**
     * @param result API 결과로 나온 리스트
     * @return DB에 맞게 리스트를 변환
     */
    private List<String> mapTags(PublicApiCategoryForm result) {
        List<String> barrierTagResult = new ArrayList<>(); // 태그 결과들을 담을 리스트
        if (result == null || result.getServList() == null) {
            return barrierTagResult;
        } else {
            PublicApiCategoryForm.ServList first = result.getServList().stream().findFirst().orElse(null);
            // Input 한 개당 하나의 배리어 프리 정보가 생성되므로 하나만 찾는다
            List<String> splitInput = getStrings(first);
            if (splitInput != null) return splitInput;
        }
        return barrierTagResult;
    }

    /**
     * @param serv API 결과
     * @return Enum을 통해 String 가공해서 변환
     */
    private List<String> getStrings(PublicApiCategoryForm.ServList serv) {
        if (serv != null && serv.validation()) { // 결과가 존재할 떄
            String[] splitInput = serv.getEvalInfo().split(",");
            return Arrays.stream(splitInput)
                    .map(BarrierCode::getColumnFromDesc)
                    .filter(code -> code != null && !code.equals(""))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public boolean start() throws IOException, InterruptedException {
        List<String[]> list = new ArrayList<>();
        BufferedReader br = null;
        File file = ResourceUtils.getFile("classpath:static/sigungu.csv");
        br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] lineContents = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            list.add(lineContents);
        }
        for (String[] strings : list) {
            System.out.println("String 프린트중 : " + Arrays.toString(strings));
            processApi(strings[0], strings[1], 1);
        }
        return true;
    }
}
