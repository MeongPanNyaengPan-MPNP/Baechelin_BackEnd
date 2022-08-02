package com.mpnp.baechelin.config.batch.util;

import com.mpnp.baechelin.api.dto.LocationInfoDto;
import com.mpnp.baechelin.api.model.BarrierCode;
import com.mpnp.baechelin.api.model.PublicApiCategoryForm;
import com.mpnp.baechelin.api.model.PublicApiV2Form;
import com.mpnp.baechelin.api.service.LocationService;
import com.mpnp.baechelin.common.DataClarification;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.storeApiUpdate.StoreApiUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
public class ApiUpdate {
    private final LocationService locationService;


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


    /**
     * @param formResult 공공 API 결과에서 각각의 Row
     */


}
