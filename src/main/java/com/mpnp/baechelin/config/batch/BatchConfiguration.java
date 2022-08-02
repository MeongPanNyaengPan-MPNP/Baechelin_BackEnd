package com.mpnp.baechelin.config.batch;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mpnp.baechelin.api.dto.LocationInfoDto;
import com.mpnp.baechelin.api.dto.LocationPartDto;
import com.mpnp.baechelin.api.model.*;
import com.mpnp.baechelin.api.service.LocationService;
import com.mpnp.baechelin.api.service.LocationServiceRT;
import com.mpnp.baechelin.common.DataClarification;
import com.mpnp.baechelin.config.batch.requestDto.JsonDTO;
import com.mpnp.baechelin.config.batch.requestDto.StoreDTO;
import com.mpnp.baechelin.config.batch.util.ApiUpdateThread;
import com.mpnp.baechelin.exception.CustomException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.store.domain.Store;
import com.mpnp.baechelin.store.repository.StoreRepository;
import com.mpnp.baechelin.store.service.StoreImageService;
import com.mpnp.baechelin.storeApiUpdate.StoreApiUpdate;
import com.mpnp.baechelin.storeApiUpdate.repository.StoreApiUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.ion.Decimal;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchConfiguration {

    @Qualifier("locationServiceRT")
    private final LocationServiceRT        locationServiceRT;
    private final StoreRepository          storeRepository;
    private final JobBuilderFactory        jobBuilderFactory;   //Job 생성자
    private final StepBuilderFactory       stepBuilderFactory;  //Step 생성자
    private final EntityManagerFactory     entityManagerFactory;
    private final StoreApiUpdateRepository storeApiUpdateRepository;
    private final StoreImageService        storeImageService;



    private static final int CHUNKSIZE = 100; //쓰기 단위인 청크사이즈



    @Bean
    public Job JpaPageJob2_batchBuild1() throws JsonProcessingException{
        return jobBuilderFactory.get("JpaPageJob2_batchBuild_save")
                .start(JpaPageJob1_step1()) // store_api_update API 응답데이터 받기
                .next(jpaPageJob1_step2())  // 추가된 업장이 있으면 store 테이블에 INSERT
//                .next(JpaPageJob2_step2())  // 사라진 업장이 있으면 store 테이블에 DELETE
                .next(JpaPageJob1_step4()) // 수정된 업장이 있다면 store 테이블에 UPDATE
                .build();
    }




    @Bean
    public Step JpaPageJob1_step1() throws JsonProcessingException{

        return stepBuilderFactory.get("JpaPageJob2_step1")
                //청크사이즈 설정
                .<StoreApiUpdate, StoreApiUpdate>chunk(CHUNKSIZE)
                .reader(jpaPageJob1_ItemReader())
                .processor(jpaPageJob1_Processor())
                .writer(jpaPageJob1_dbItemWriter())
                .build();

    }

    @Bean
    public ListItemReader<StoreApiUpdate> jpaPageJob1_ItemReader() throws JsonProcessingException{

        List<StoreApiUpdate> storeApiUpdateList = new ArrayList<>();

        List<List<String>> csvList = readCSVFile("src/main/resources/static/sigungu.csv");
        List<List<List<String>>> csvListList = Lists.partition(csvList, csvList.size()/30);


        List<ApiUpdateThread> apiUpdateThreadList = new ArrayList<>();
        int index = 1;
        for(List<List<String>> csvListAvg: csvListList){
            ApiUpdateThread apiUpdateThread = new ApiUpdateThread(csvListAvg, storeApiUpdateList, 1, publicV2Key2, kokoaApiKey, index);
            apiUpdateThread.start();
            apiUpdateThreadList.add(apiUpdateThread);
            index ++;
        }

        try {
            for (ApiUpdateThread apiUpdateThread: apiUpdateThreadList){
                apiUpdateThread.join();

            }
        } catch(Exception e){
            e.printStackTrace();
        }

        log.info("store SIZE --> "+ storeApiUpdateList.size());

        HttpHeaders  headers = new HttpHeaders();
        RestTemplate rest    = new RestTemplate();
        String body          = "";

        HttpEntity<String>      requestEntity  = new HttpEntity<String>(body, headers);
        ResponseEntity<String>  responseEntity = rest.exchange("http://openapi.seoul.go.kr:8088/5274616b45736f7933376e6c525658/json/touristFoodInfo/1/1000/", HttpMethod.GET, requestEntity, String.class);
        HttpStatus              httpStatus     = responseEntity.getStatusCode();
        String                  response       = responseEntity.getBody();


        JsonDTO jsonDTO = new Gson().fromJson(response, JsonDTO.class);  //conversion using Gson Library.
        setInfos(jsonDTO);

        storeApiUpdateList.addAll(saveValidStores(jsonDTO.getTouristFoodInfo().getRow()));


        log.info("store SIZE --> "+ storeApiUpdateList.size());

        return new ListItemReader<>(storeApiUpdateList);


    }

    private ItemProcessor<StoreApiUpdate, StoreApiUpdate> jpaPageJob1_Processor() {
        return storeApiUpdate -> {

            log.info(storeApiUpdate.toString());

            log.info("********** This is unPaidMemberProcessor");
            return storeApiUpdate;

        };
    }
    @Bean
    public ItemWriter<StoreApiUpdate> jpaPageJob1_dbItemWriter(){


        log.info("********** This is unPaidStoreWriter");

        return list -> {
            for(StoreApiUpdate storeApiUpdate: list){
                if(!storeApiUpdateRepository.existsById(storeApiUpdate.getId())){
                    storeImageService.saveImage(storeApiUpdate.getId());
                }
            }
            storeApiUpdateRepository.saveAll(list);
        };

    }







    @Bean
    public Step jpaPageJob1_step2() throws JsonProcessingException {
        return stepBuilderFactory.get("JpaPageJob2_step2")
                //청크사이즈 설정
                .<StoreApiUpdate, Store>chunk(CHUNKSIZE)
                .reader(jpaPageJob1_step2_ItemReader())
                .processor(jpaPageJob1_step2_Processor())
                .writer(jpaPageJob1_step2_dbItemWriter())
                .build();

    }

    @Bean
    public JpaPagingItemReader<StoreApiUpdate> jpaPageJob1_step2_ItemReader() throws JsonProcessingException {

        log.info("********** This is unPaidStoreReader");
        return new JpaPagingItemReaderBuilder<StoreApiUpdate>()
                .name("jpaPageJob3_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNKSIZE)
                .queryString("select a from Store_api_update a left join Store b on a.id = b.id where b.id is null order by a.id asc")
                .build();
    }


    private ItemProcessor<StoreApiUpdate, Store> jpaPageJob1_step2_Processor() {
        log.info("********** This is unPaidStoreProcessor");
        return storeApiUpdate -> {
            return new Store(storeApiUpdate);
        };

    }


    private ItemWriter<Store> jpaPageJob1_step2_dbItemWriter() {
        log.info("********** This is jpaPageJob3_dbItemWriter");
        JpaItemWriter<Store> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }







    @Bean
    public Step JpaPageJob4_step1() throws JsonProcessingException {
        return stepBuilderFactory.get("JpaPageJob4_step2")
                //청크사이즈 설정
                .<Store, Store>chunk(CHUNKSIZE)
                .reader(jpaPageJob4_ItemReader())
                .processor(jpaPageJob4_Processor())
                .writer(jpaPageJob4_dbItemWriter())
                .build();

    }

    @Bean
    public JpaPagingItemReader<Store> jpaPageJob4_ItemReader() throws JsonProcessingException {

        log.info("********** This is unPaidStoreReader");
        return new JpaPagingItemReaderBuilder<Store>()
                .name("jpaPageJob3_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNKSIZE)
                .queryString("select a from Store a left join Store_api_update b on a.id = b.id where b.id is null order by a.id ASC")
                .build();
    }




    private ItemProcessor<Store, Store> jpaPageJob4_Processor() {
        log.info("********** This is unPaidStoreProcessor");
        return new ItemProcessor<Store, Store>() {  //

            @Override
            public Store process(Store store) throws Exception {
                log.info("********** This is unPaidMemberProcessor");
                return store;  // 2

            }
        };

    }


    private ItemWriter<Store> jpaPageJob4_dbItemWriter() {
        log.info("********** This is jpaPageJob3_dbItemWriter");

        return ((List<? extends Store> storeList) -> storeRepository.deleteAll(storeList));
    }


    @Bean
    public Step JpaPageJob1_step4() throws JsonProcessingException {
        return stepBuilderFactory.get("JpaPageJob5_step1")
                //청크사이즈 설정
                .<StoreApiUpdate, Store>chunk(CHUNKSIZE)
                .reader(JpaPageJob1_step4_ItemReader())
                .processor(JpaPageJob1_step4_Processor())
                .writer(JpaPageJob1_step4_dbItemWriter())
                .build();
    }



    @Bean
    public JpaPagingItemReader<StoreApiUpdate> JpaPageJob1_step4_ItemReader() throws JsonProcessingException {

        log.info("********** This is unPaidStoreReader");
        System.out.println("=============READER=============");
        return new JpaPagingItemReaderBuilder<StoreApiUpdate>()
                .name("jpaPageJob5_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNKSIZE)
                .queryString("select a from Store_api_update a join Store b on a.id = b.id where a.id = b.id \n" +
                        "and a.approach != b.approach \n" +
                        "or a.address != b.address \n" +
                        "or a.elevator != b.elevator  \n" +
                        "or a.latitude != b.latitude \n" +
                        "or a.longitude != b.longitude \n" +
                        "or a.name != b.name \n" +
                        "or a.parking != b.parking \n" +
                        "or a.phoneNumber != b.phoneNumber\n" +
                        "or a.heightDifferent != b.heightDifferent \n" +
                        "or a.toilet != b.toilet order by a.id asc")
                .build();
    }


    private ItemProcessor<StoreApiUpdate, Store> JpaPageJob1_step4_Processor() {
        log.info("********** This is unPaidStoreProcessor");
        System.out.println("=============PROCESSOR=============");
        return storeApiUpdate -> {
            System.out.println("============="+storeApiUpdate.getId()+"=============");

            Optional<Store> store = storeRepository.findById(storeApiUpdate.getId());
            store.get().apiUpdate(storeApiUpdate);

            return store.get();
        };
    }


    private ItemWriter<Store> JpaPageJob1_step4_dbItemWriter() {
        log.info("********** This is jpaPageJob3_dbItemWriter");
        System.out.println("=============WRITER=============");

//        return ((List<? extends Store> storeList) -> storeRepository.saveAll(storeList));
        JpaItemWriter<Store> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;

    }


//
//    private JsonDTO setInfos(JsonDTO jsonDTO) {
//        jsonDTO.getTouristFoodInfo().getRow().forEach(row -> {
//                    try {
//                        if (!setRowLngLat(row)) {
//                            System.out.println("!setRowLngLat(row) --> return");
//                            return;
//                        }
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        setRowCategoryAndId(row);
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//        );
//        return jsonDTO;
//    }
//
//


//
//
//    private boolean storeValidation(StoreDTO row) {
//        return row.getLatitude() != null && row.getLongitude() != null && row.getCategory() != null && row.getStoreId() != null;
//    }
//
//
//    private void setRowCategoryAndId(StoreDTO row) throws JsonProcessingException {
//        LocationKeywordSearchForm categorySearchForm = locationService.giveCategoryByLatLngKeywordRest(String.valueOf(row.getLatitude()), String.valueOf(row.getLongitude()), row.getSISULNAME());
////        LocationKeywordSearchForm categorySearchForm = locationService.giveCategoryByLatLngKeyword(row.getLatitude(), row.getLongitude(), row.getSISULNAME());
//        LocationKeywordSearchForm.Documents categoryDoc = Arrays.stream(categorySearchForm.getDocuments()).findFirst().orElse(null);
//        if (categoryDoc == null || !Arrays.asList("FD6", "CE7").contains(categoryDoc.getCategory_group_code()))
//            return;
//        row.setStoreId(Integer.parseInt(categoryDoc.getId()));
//        row.setSISULNAME(categoryDoc.getPlace_name());
//        row.setCategory(categoryFilter(Optional.of(categoryDoc.getCategory_name()).orElse(null)));
//    }
//
//
//    private boolean setRowLngLat(StoreDTO row) throws JsonProcessingException {
//
//        LocationKeywordSearchForm latLngSearchForm = locationService.giveLatLngByAddressRest(row.getADDR());
//        if (latLngSearchForm == null) return false;
//        LocationKeywordSearchForm.Documents latLngDoc = Arrays.stream(latLngSearchForm.getDocuments()).findFirst().orElse(null);
//        if (latLngDoc == null) return false;
//
//        row.setLatitude(Decimal.valueOf(latLngDoc.getY()));
//        row.setLongitude(Decimal.valueOf(latLngDoc.getX()));
//
//        // 카테고리 ENUM으로 전환하기
//        row.setCategory(categoryFilter(Optional.of(latLngDoc.getCategory_name()).orElse("기타")));
//
//
//        return true;
//    }
//


//
//    private String categoryFilter(String category) {
//        if (category == null) {
//            return Category.ETC.getDesc();
//        } else if (category.contains(">")) {
//            return Category.giveCategory(category.split(" > ")[1]).getDesc();
//        } else {
//            return null;
//        }
//    }


    @Value("${kakao.api.key}")
    private String kokoaApiKey;

    @Value("${public.api.v2.key}")
    private String publicV2Key;
    @Value("${public.api.v2.key2}")
    private String publicV2Key2;


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
//    public List<List<Store>> processApi(String siDoNm, String cggNm, int pageNo) {
//        // 헤더 세팅
//        HttpHeaders headers = setHttpHeaders();
//        log.info("{}, {}, print", siDoNm, cggNm);
//        // URI 생성
//        String publicV2Uri = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getDisConvFaclList";
//        URI uri = UriComponentsBuilder
//                .fromUriString(publicV2Uri)
//                .queryParam("serviceKey", publicV2Key3)
//                .queryParam("numOfRows", "1000")
//                .queryParam("pageNo", String.valueOf(pageNo))
//                .queryParam("siDoNm", siDoNm)
//                .queryParam("cggNm", cggNm)
//                .queryParam("faclTyCd", "UC0B01")
//                .encode()
//                .build()
//                .toUri();
//
//        RestTemplate restTemplate = new RestTemplate();
//        log.warn(uri.toString());
//        ResponseEntity<PublicApiV2Form> resultRe = restTemplate.exchange(
//                uri, HttpMethod.GET, new HttpEntity<>(headers), PublicApiV2Form.class
//        );
//        PublicApiV2Form result = resultRe.getBody();
//        if (result == null) // 결과가 없으면 false 리턴
//            return null;
//        return processForm(result);
//        // totalSize, 현 페이지를 통해 다음 페이지가 있는지 확인하고 T/F 리턴
//    }

    /**
     * @param formResult 공공 API 결과에서 각각의 Row
     */
//    public List<List<Store>> processForm(PublicApiV2Form formResult) {
//        if (formResult == null || formResult.getServList() == null) return null;
//        // servList + Barrier Free Tag  + category
//
//        List<List<Store>> storeListList = new ArrayList<>();
//
//        for (PublicApiV2Form.ServList servList : formResult.getServList()) {
//            // servList 요소 - 각각의 배리어 프리 업장 하나하나를 검증
//            if (!servList.validateServList()) continue;
//
//            List<Store> storeList = mapApiToStoreWithPaging(servList);
//
//            if (storeList == null) continue;
//
//            storeListList.add(storeList);
//        }
//        // 검증 완료된 store들을 저장
//        return storeListList;
//    }

    /**
     * @param servList V2의 결과 Row
     */
//    private List<Store> mapApiToStoreWithPaging(PublicApiV2Form.ServList servList) {
//        // 태그 String을 분리 & 매핑해 리스트에 저장
//        List<String> barrierTagList = tagStrToList(servList.getWfcltId());
//
//        // TODO 태그가 비어있다면 어떻게 해야 할 지 ? -> 저장 혹은 버리기 (현재 버리기로 구현)
//        if (barrierTagList.isEmpty()) return null;
//
//        /*
//         * 주소 + 위/경도를 사용해 해당 건물의 배리어 프리 매장들을
//         * 등록하도록 변경             */
////        if (searchWithStoreName(servList, barrierTagList)) return;
//        // 검색 결과가 없을 경우
//        return searchWithAddress(servList, barrierTagList);
//    }

    /**
     * @param servList       대상 Row
     * @param barrierTagList 배리어 태그 리스트
     * @return 검색 결과 존재 여부
     */
//    @Transactional
//    boolean searchWithStoreName(PublicApiV2Form.ServList servList, List<String> barrierTagList) {
//        LocationInfoDto.LocationResponse resultDto =
//                locationService.convertGeoAndStoreNameToKeyword(servList.getFaclLat(), servList.getFaclLng(), servList.getFaclNm());
//        if (resultDto == null)
//            return false;
//        Store nStore = new Store(resultDto, servList, barrierTagList);
//        if (!storeRepository.existsById(nStore.getId())) {
//            storeRepository.saveAndFlush(nStore);
//            storeImageService.saveImage(nStore.getId());
//        }
//        return true;
//    }

    /**
     * @param servList       대상 Row
     * @param barrierTagList 배리어 태그 리스트
     */
//    @Transactional
//    public List<Store> searchWithAddress(PublicApiV2Form.ServList servList, List<String> barrierTagList) {
//        List<LocationInfoDto.LocationResponse> locationResponseMapList = locationService
//                .convertGeoAndAddressToKeyword(servList.getFaclLat(), servList.getFaclLng(), DataClarification.clarifyString(servList.getLcMnad()));
//        List<Store> storeList =  new ArrayList<>();
//        for (LocationInfoDto.LocationResponse locationResponse : locationResponseMapList) {
//            Store nStore = new Store(locationResponse, servList, barrierTagList);
//
//            storeList.add(nStore);
////            // ID 값으로 store 중복 검사해 중복되지 않을 시에만 리스트에 저장
////            if (!storeRepository.existsById(nStore.getId())) {
////                storeRepository.saveAndFlush(nStore);
////                storeImageService.saveImage(nStore.getId());
////            }
//        }
//        return storeList;
//    }

    /**
     * @param sisulNum 시설 고유 번호
     * @return API 결과로 나온 문자열을 리스트로 분리
     */
    public List<String> tagStrToList(String sisulNum) {
        HttpHeaders headers = setHttpHeaders();
        String publicV2CategoryUri = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getFacInfoOpenApiJpEvalInfoList";
        URI uri = UriComponentsBuilder
                .fromUriString(publicV2CategoryUri)
                .queryParam("serviceKey", publicV2Key2)
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



//    public void start() throws IOException, InterruptedException {
//        List<String[]> list = new ArrayList<>();
//        BufferedReader br = null;
//        File file = ResourceUtils.getFile("classpath:static/sigungu.csv");
//        br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
//        String line = null;
//        while ((line = br.readLine()) != null) {
//            String[] lineContents = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//            list.add(lineContents);
//        }
//        for (String[] strings : list) {
//            System.out.println("String 프린트중 : " + Arrays.toString(strings));
//            processApi(strings[0], strings[1], 1);
//        }
//    }

    /** CSV 파일 읽기 */
    private List<List<String>> readCSVFile(String filePath){
        List<List<String>> csvList = new ArrayList<>();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = Files.newBufferedReader(Paths.get(filePath));
            String line = "";

            while ((line = bufferedReader.readLine()) != null){
                List<String> stringList = new ArrayList<>();
                String stringArray[] = line.split(",");

                stringList = Arrays.asList(stringArray);
                csvList.add(stringList);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert bufferedReader != null;
                bufferedReader.close();

            } catch(IOException e) {
                e.printStackTrace();
            }
            return csvList;
        }
    }





    /**
     * @param jsonDTO API 호출 결과
     */
    private void setInfos(JsonDTO jsonDTO) {
        jsonDTO.getTouristFoodInfo().getRow().forEach(row -> {
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
    private boolean setRowLngLat(StoreDTO row) throws JsonProcessingException {
        LocationPartDto.LatLong latLong = locationServiceRT.convertAddressToGeo(row.getADDR());
        if (latLong == null || !latLong.validate()) return false;
        row.setLatitude(Decimal.valueOf(latLong.getLatitude()));
        row.setLongitude(Decimal.valueOf(latLong.getLongitude()));
        return true;
    }


    /**
     * @param row 행 하나하나
     * @throws JsonProcessingException JSON 파싱, 매핑 오류시 발생하는 Exception
     */
    private void setRowCategoryAndId(StoreDTO row) throws JsonProcessingException {
        LocationInfoDto.LocationResponse locationResponse = locationServiceRT
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
    public List<StoreApiUpdate> saveValidStores(List<StoreDTO> rows) {
        List<StoreApiUpdate> storeList = rows.stream().filter(StoreDTO::validation)
                .map(StoreApiUpdate::new).collect(Collectors.toList());


        return storeList;
    }


}



