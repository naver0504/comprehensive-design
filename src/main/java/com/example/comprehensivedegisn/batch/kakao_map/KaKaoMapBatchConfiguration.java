package com.example.comprehensivedegisn.batch.kakao_map;

import com.example.comprehensivedegisn.batch.kakao_map.api.KaKaoApiClient;
import com.example.comprehensivedegisn.batch.kakao_map.api.dto.ApartmentGeoRecord;
import com.example.comprehensivedegisn.domain.ApartmentTransaction;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Configuration
@EnableConfigurationProperties(KaKaoRestApiProperties.class)
@RequiredArgsConstructor
@Slf4j
public class KaKaoMapBatchConfiguration {

    private final String JOB_NAME = "kakaoMapJob";
    private final String STEP_NAME = JOB_NAME + "_step";
    private int chunkSize = 1000;

    private final EntityManagerFactory emf;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final KaKaoRestApiProperties kaKaoRestApiProperties;
    private final RoadNameCacheRepository  roadNameCacheRepository;
    private final DataSource dataSource;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<ApartmentTransaction> jpaApartmentTransactionReader(@Value("#{jobParameters[regionalCode]}") String guCode) {
        JpaPagingItemReader<ApartmentTransaction> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(emf);
        reader.setQueryString("SELECT a FROM ApartmentTransaction a join DongEntity d on a.dongEntity.id = d.id where d.guCode = :guCode order by a.id asc");
        reader.setParameterValues(Map.of("guCode", guCode));
        reader.setPageSize(chunkSize);
        return reader;
    }

    @Bean
    public AsyncItemProcessor<ApartmentTransaction, ApartmentGeoRecord> asyncKaKaoMapProcessor(KaKaoApiClient kaKaoApiClient) {
        AsyncItemProcessor<ApartmentTransaction, ApartmentGeoRecord> asyncItemProcessor = new AsyncItemProcessor<>();
        //create fixed thread pool size 10
        asyncItemProcessor.setTaskExecutor(taskExecutor());
        asyncItemProcessor.setDelegate(kaKaoMapProcessor(kaKaoApiClient));
        return asyncItemProcessor;
    }

    //queue 가 꽉 차야지 maxPoolSize 만큼 thread 를 생성한다.
    //하지만 기본 값이 Integer.MAX_VALUE 이기 때문에 queue 가 꽉 차지 않는 한 maxPoolSize 만큼 thread 를 생성하지 않는다.
    //적절한 queueCapacity 를 설정해야 하지만 단순한 작업이기 때문에
    //corePoolSize만 사용하도록 설정한다.

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        // size 5부터 메모리 사용량 94퍼
        threadPoolTaskExecutor.setCorePoolSize(5);

        //corePoolSize 만큼 thread 를 미리 생성한다.
        threadPoolTaskExecutor.setPrestartAllCoreThreads(true);
        return threadPoolTaskExecutor;
    }


    @Bean
    @StepScope
    public KaKaoApiClient kaKaoApiClient() {
        return new KaKaoApiClient(restTemplate(), roadNameCacheRepository, kaKaoRestApiProperties);
    }

    @Bean
    public ItemProcessor<ApartmentTransaction, ApartmentGeoRecord> kaKaoMapProcessor(KaKaoApiClient kaKaoApiClient) {
        return kaKaoApiClient::getGeoLocation;
    }

    @Bean
    public ItemWriter<Future<ApartmentGeoRecord>> kaKaoMapWriter() {


        return futures -> {

            List<ApartmentGeoRecord> apartmentGeoRecords = futures.getItems().stream().map(future -> {
                try {
                    return future.get();
                } catch (Exception e) {
                    log.error("Error while getting future", e);
                    throw new IllegalStateException(e);
                }
            }).toList();


            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("""
                    update apartment_transaction set x = ?, y = ? where id = ?
                    """.trim());

            try {
                log.info("start idx : {}", apartmentGeoRecords.get(0).id());
                for (ApartmentGeoRecord apartmentGeoRecord : apartmentGeoRecords) {
                    statement.setString(1, apartmentGeoRecord.x());
                    statement.setString(2, apartmentGeoRecord.y());
                    statement.setLong(3, apartmentGeoRecord.id());
                    statement.addBatch();
                }
                statement.executeBatch();
            } catch (Exception e) {
                throw e;
            } finally {
                if (!statement.isClosed()) {
                    statement.close();
                }
                if (!connection.isClosed()) {
                    connection.close();
                }
            }
        };
    }


    @Bean(name = STEP_NAME)
    public Step kaKaoMapStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<ApartmentTransaction, Future<ApartmentGeoRecord>>chunk(chunkSize, platformTransactionManager)
                .reader(jpaApartmentTransactionReader(null))
                .processor(asyncKaKaoMapProcessor(kaKaoApiClient()))
                .writer(kaKaoMapWriter())
                .build();
    }

    @Bean(name = JOB_NAME)
    public Job kaKaoMapJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(kaKaoMapStep())
                .build();
    }
}
