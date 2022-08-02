package com.mpnp.baechelin.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mpnp.baechelin.config.batch.BatchConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class SchedulerConfiguration {
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .withTimeZone(TimeZone.getTimeZone("Asia/Seoul"))
                        .build()
        );
    }



    private final JobLauncher jobLauncher;
    private final BatchConfiguration batchConfiguration;
    @Scheduled(cron = "0 30 4 * * ?",zone = "Asia/Seoul")
    public void storeApiUpdate() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.getHour() + ":" + now.getMinute() + ":" + now.getSecond());

        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));

        try{
            jobLauncher.run(batchConfiguration.JpaPageJob2_batchBuild1(), new JobParameters());
        }catch(JobExecutionAlreadyRunningException
               | JobInstanceAlreadyCompleteException
               | JobParametersInvalidException
               | org.springframework.batch.core.repository.JobRestartException e){

            log.error(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}

