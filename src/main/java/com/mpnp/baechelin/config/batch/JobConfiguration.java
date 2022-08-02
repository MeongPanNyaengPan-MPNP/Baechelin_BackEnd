package com.mpnp.baechelin.config.batch;//package com.mpnp.baechelin.config.batch;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.google.gson.Gson;
//import com.mpnp.baechelin.api.dto.PublicApiResponseDto;
//import com.mpnp.baechelin.api.model.LocationKeywordSearchForm;
//import com.mpnp.baechelin.api.service.LocationService;
//import com.mpnp.baechelin.config.batch.requestDto.JsonDTO;
//import com.mpnp.baechelin.config.batch.requestDto.StoreDTO;
//import com.mpnp.baechelin.store.domain.Category;
//import com.mpnp.baechelin.store.domain.Store;
//import com.mpnp.baechelin.store.repository.StoreRepository;
//import com.mpnp.baechelin.storeApiUpdate.StoreApiUpdate;
//import com.mpnp.baechelin.storeApiUpdate.repository.StoreApiUpdateRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.database.JpaItemWriter;
//import org.springframework.batch.item.database.JpaPagingItemReader;
//import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
//import org.springframework.batch.item.support.ListItemReader;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.*;
//import org.springframework.web.client.RestTemplate;
//import software.amazon.ion.Decimal;
//
//import javax.persistence.EntityManagerFactory;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//@RequiredArgsConstructor
//@Configuration
//public class JobConfiguration {
//
//    private final LocationService          locationService;
//    private final StoreRepository          storeRepository;
//    private final JobBuilderFactory        jobBuilderFactory;   //Job 생성자
//    private final StepBuilderFactory       stepBuilderFactory;  //Step 생성자
//    private final EntityManagerFactory     entityManagerFactory;
//    private final StoreApiUpdateRepository storeApiUpdateRepository;
//
//
//
//    private static final int CHUNKSIZE = 10; //쓰기 단위인 청크사이즈
//
//
//
//    @Bean
//    public Job JpaPageJob2_batchBuild1() throws JsonProcessingException {
//        return jobBuilderFactory.get("JpaPageJob2_batchBuild_save")
//                .start(JpaPageJob2_step1())
//                .next(JpaPageJob2_step2()) // update insert
//                .next(JpaPageJob4_step1()) // update delete
//                .next(JpaPageJob5_step1()) // update
//                .build();
//    }
//
//
//
//
//    @Bean
//    public Step JpaPageJob2_step1() throws JsonProcessingException {
//
//        return stepBuilderFactory.get("JpaPageJob2_step1")
//                //청크사이즈 설정
//                .<StoreApiUpdate, StoreApiUpdate>chunk(CHUNKSIZE)
//                .reader(jpaPageJob2_ItemReader())
//                .processor(jpaPageJob2_Processor())
//                .writer(jpaPageJob2_dbItemWriter())
//                .build();
//
//    }
//
//
//    @Bean
//    public ListItemReader<StoreApiUpdate> jpaPageJob2_ItemReader() throws JsonProcessingException {
//
//        log.info("********** This is unPaidStoreReader");
//
//        HttpHeaders  headers = new HttpHeaders();
//        RestTemplate rest    = new RestTemplate();
//        String body          = "";
//
//        HttpEntity<String>      requestEntity  = new HttpEntity<String>(body, headers);
//        ResponseEntity<String>  responseEntity = rest.exchange("http://openapi.seoul.go.kr:8088/5274616b45736f7933376e6c525658/json/touristFoodInfo/1/1000/", HttpMethod.GET, requestEntity, String.class);
//        HttpStatus              httpStatus     = responseEntity.getStatusCode();
//        String                  response       = responseEntity.getBody();
//        int                     status         = httpStatus.value();
//
//
//        JsonDTO jsonDTO = new Gson().fromJson(response, JsonDTO.class);  //conversion using Gson Library.
//        setInfos(jsonDTO);
//
//        List<StoreApiUpdate> storeList = jsonDTO.getTouristFoodInfo().getRow().stream().filter(this::storeValidation).map(StoreApiUpdate::new).collect(Collectors.toList());
//        System.out.println(storeList.size());
//
//        return new ListItemReader<>(storeList);
//    }
//
//
//    private ItemProcessor<StoreApiUpdate, StoreApiUpdate> jpaPageJob2_Processor() {
//        return new ItemProcessor<StoreApiUpdate, StoreApiUpdate>() {  //
//
//            @Override
//            public StoreApiUpdate process(StoreApiUpdate storeApiUpdate) throws Exception {
//
//                log.info("********** This is unPaidMemberProcessor");
//                return storeApiUpdate;  // 2
//
//            }
//        };
//    }
//
//    @Bean
//    public ItemWriter<StoreApiUpdate> jpaPageJob2_dbItemWriter(){
//
//
//        log.info("********** This is unPaidStoreWriter");
//
//        return ((List<? extends StoreApiUpdate> storeList) -> storeApiUpdateRepository.saveAll(storeList));
//    }
//
//
//
//    @Bean
//    public Step JpaPageJob2_step2() throws JsonProcessingException {
//        return stepBuilderFactory.get("JpaPageJob2_step2")
//                //청크사이즈 설정
//                .<StoreApiUpdate, Store>chunk(CHUNKSIZE)
//                .reader(jpaPageJob3_ItemReader())
//                .processor(jpaPageJob3_Processor())
//                .writer(jpaPageJob3_dbItemWriter())
//                .build();
//
//    }
//
//    @Bean
//    public JpaPagingItemReader<StoreApiUpdate> jpaPageJob3_ItemReader() throws JsonProcessingException {
//
//        log.info("********** This is unPaidStoreReader");
//        return new JpaPagingItemReaderBuilder<StoreApiUpdate>()
//                .name("jpaPageJob3_dbItemReader")
//                .entityManagerFactory(entityManagerFactory)
//                .pageSize(CHUNKSIZE)
//                .queryString("select a from store_api_update a left join store b on a.id = b.id where b.id is null order by a.id asc")
//                .build();
//    }
//
//
//    private ItemProcessor<StoreApiUpdate, Store> jpaPageJob3_Processor() {
//        log.info("********** This is unPaidStoreProcessor");
//        return storeApiUpdate -> {
//            return new Store(storeApiUpdate);
//        };
//
//    }
//
//
//    private ItemWriter<Store> jpaPageJob3_dbItemWriter() {
//        log.info("********** This is jpaPageJob3_dbItemWriter");
//        JpaItemWriter<Store> jpaItemWriter = new JpaItemWriter<>();
//        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
//        return jpaItemWriter;
//    }
//
//
//
//
//
//
//
//    @Bean
//    public Step JpaPageJob4_step1() throws JsonProcessingException {
//        return stepBuilderFactory.get("JpaPageJob4_step2")
//                //청크사이즈 설정
//                .<Store, Store>chunk(CHUNKSIZE)
//                .reader(jpaPageJob4_ItemReader())
//                .processor(jpaPageJob4_Processor())
//                .writer(jpaPageJob4_dbItemWriter())
//                .build();
//
//    }
//
//    @Bean
//    public JpaPagingItemReader<Store> jpaPageJob4_ItemReader() throws JsonProcessingException {
//
//        log.info("********** This is unPaidStoreReader");
//        return new JpaPagingItemReaderBuilder<Store>()
//                .name("jpaPageJob3_dbItemReader")
//                .entityManagerFactory(entityManagerFactory)
//                .pageSize(CHUNKSIZE)
//                .queryString("select a from store a left join store_api_update b on a.id = b.id where b.id is null order by a.id ASC")
//                .build();
//    }
//
//
//
//
//    private ItemProcessor<Store, Store> jpaPageJob4_Processor() {
//        log.info("********** This is unPaidStoreProcessor");
//        return new ItemProcessor<Store, Store>() {  //
//
//            @Override
//            public Store process(Store store) throws Exception {
//                log.info("********** This is unPaidMemberProcessor");
//                return store;  // 2
//
//            }
//        };
//
//    }
//
//
//    private ItemWriter<Store> jpaPageJob4_dbItemWriter() {
//        log.info("********** This is jpaPageJob3_dbItemWriter");
//
//        return ((List<? extends Store> storeList) -> storeRepository.deleteAll(storeList));
//    }
//
//
//    @Bean
//    public Step JpaPageJob5_step1() throws JsonProcessingException {
//        return stepBuilderFactory.get("JpaPageJob5_step1")
//                //청크사이즈 설정
//                .<StoreApiUpdate, Store>chunk(CHUNKSIZE)
//                .reader(jpaPageJob5_ItemReader())
//                .processor(jpaPageJob5_Processor())
//                .writer(jpaPageJob5_dbItemWriter())
//                .build();
//
//    }
//
//
//
//
//
//    @Bean
//    public JpaPagingItemReader<StoreApiUpdate> jpaPageJob5_ItemReader() throws JsonProcessingException {
//
//        log.info("********** This is unPaidStoreReader");
//        System.out.println("=============READER=============");
//        return new JpaPagingItemReaderBuilder<StoreApiUpdate>()
//                .name("jpaPageJob5_dbItemReader")
//                .entityManagerFactory(entityManagerFactory)
//                .pageSize(CHUNKSIZE)
//                .queryString("select a from store_api_update a join store b on a.id = b.id where a.id = b.id \n" +
//                        "and a.approach != b.approach \n" +
//                        "or a.category != b.category \n" +
//                        "or a.address != b.address \n" +
//                        "or a.elevator != b.elevator  \n" +
//                        "or a.latitude != b.latitude \n" +
//                        "or a.longitude != b.longitude \n" +
//                        "or a.name != b.name \n" +
//                        "or a.parking != b.parking \n" +
//                        "or a.phoneNumber != b.phoneNumber\n" +
//                        "or a.heightDifferent != b.heightDifferent \n" +
//                        "or a.pointAvg != b.pointAvg \n" +
//                        "or a.reviewCount != b.reviewCount \n" +
//                        "or a.toilet != b.toilet order by a.id asc")
//                .build();
//    }
//
//
//    private ItemProcessor<StoreApiUpdate, Store> jpaPageJob5_Processor() {
//        log.info("********** This is unPaidStoreProcessor");
//        System.out.println("=============PROCESSOR=============");
//        return storeApiUpdate -> {
//            System.out.println("============="+storeApiUpdate.getId()+"=============");
//
//            Optional<Store> store = storeRepository.findById(storeApiUpdate.getId());
//            store.get().apiUpdate(storeApiUpdate);
//
//            return store.get();
//        };
//    }
//
//
//    private ItemWriter<Store> jpaPageJob5_dbItemWriter() {
//        log.info("********** This is jpaPageJob3_dbItemWriter");
//        System.out.println("=============WRITER=============");
//
//        return ((List<? extends Store> storeList) -> storeRepository.saveAll(storeList));
//
//
//    }
//
//
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
//
//
//
//
//
//
//
//}


