package com.mpnp.baechelin.config.batch.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.mpnp.baechelin.config.batch.BatchConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableSchedulerLock(defaultLockAtMostFor = "PT10S")
public class BatchScheduler {

    private final BatchConfiguration batchConfiguration;

    private final JobLauncher jobLauncher;

//    @Scheduled(cron = "0 30 4 1 1/1 ? *",zone = "Asia/Seoul")
//    @SchedulerLock(name = "updateScheduler", lockAtLeastFor = "PT58M", lockAtMostFor = "PT59M")
//    public void storeApiUpdateJob() {
//
//        LocalDateTime now = LocalDateTime.now();
//        System.out.println(now.getHour() + ":" + now.getMinute() + ":" + now.getSecond());
//
//        Map<String, JobParameter> confMap = new HashMap<>();
//        confMap.put("time", new JobParameter(System.currentTimeMillis()));
//        JobParameters jobParameters = new JobParameters(confMap);
//        try{
//            jobLauncher.run(batchConfiguration.JpaPageJob1_storeApiUpdate(), jobParameters);
//        }catch(JobExecutionAlreadyRunningException
//               | JobInstanceAlreadyCompleteException
//               | JobParametersInvalidException
//               | org.springframework.batch.core.repository.JobRestartException e){
//
//            log.error(e.getMessage());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
