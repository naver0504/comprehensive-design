package com.example.comprehensivedegisn.batch.kakao_map.jibun;

import com.example.comprehensivedegisn.batch.CacheRepository;
import com.example.comprehensivedegisn.batch.kakao_map.KaKaoMapBaseConfiguration;
import com.example.comprehensivedegisn.batch.kakao_map.dto.ApartmentGeoRecord;
import com.example.comprehensivedegisn.batch.kakao_map.dto.LocationRecord;
import com.example.comprehensivedegisn.batch.kakao_map.KaKaoRestApiProperties;
import com.example.comprehensivedegisn.batch.kakao_map.dto.TransactionWithGu;
import com.example.comprehensivedegisn.batch.query_dsl.QuerydslNoOffsetIdPagingItemReader;
import com.example.comprehensivedegisn.batch.query_dsl.expression.Expression;
import com.example.comprehensivedegisn.batch.query_dsl.options.QuerydslNoOffsetNumberOptions;
import com.example.comprehensivedegisn.domain.ApartmentTransaction;
import com.querydsl.core.types.Projections;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Future;

import static com.example.comprehensivedegisn.domain.QApartmentTransaction.apartmentTransaction;
import static com.example.comprehensivedegisn.domain.QDongEntity.dongEntity;

@Configuration
@EnableConfigurationProperties(KaKaoRestApiProperties.class)
@RequiredArgsConstructor
@Import(KaKaoMapBaseConfiguration.class)
public class KaKaoMapBatchConfigurationWithJibun {

    private final String JOB_NAME = "kakaoMapJobWithJibun";
    private final String STEP_NAME = JOB_NAME + "_step";
    private int chunkSize = 1000;

    private final EntityManagerFactory emf;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final KaKaoRestApiProperties kaKaoRestApiProperties;

    private final ItemWriter<Future<ApartmentGeoRecord>> kaKaoMapWriter;
    private final RestTemplate restTemplate;
    private final CacheRepository<String, LocationRecord> cacheRepository;
    private final TaskExecutor taskExecutor;


    @Bean(name = STEP_NAME + " KaKaoApiClient")
    @StepScope
    public KaKaoApiClientWithJibun kaKaoApiClient() {
        return new KaKaoApiClientWithJibun(kaKaoRestApiProperties, restTemplate, cacheRepository);
    }


    public JpaPagingItemReader<ApartmentTransaction> jpaApartmentTransactionReader() {
        JpaPagingItemReader<ApartmentTransaction> reader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };
        reader.setEntityManagerFactory(emf);
        reader.setQueryString("SELECT a FROM ApartmentTransaction a where a.x is null and a.y is null order by a.id asc");
        reader.setPageSize(chunkSize);
        return reader;
    }

    @Bean(name = STEP_NAME + " Reader")
    @StepScope
    public QuerydslNoOffsetIdPagingItemReader<TransactionWithGu, Long> querydslPagingItemReader() {

        QuerydslNoOffsetNumberOptions<TransactionWithGu, Long> options =
                new QuerydslNoOffsetNumberOptions<>(apartmentTransaction.id, Expression.ASC);

        return new QuerydslNoOffsetIdPagingItemReader<>(emf, chunkSize, options, query -> query
                .select(Projections.constructor(
                        TransactionWithGu.class,
                        apartmentTransaction.id,
                        dongEntity.gu,
                        apartmentTransaction.dong,
                        apartmentTransaction.jibun)
                )
                .from(apartmentTransaction)
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.eq(dongEntity))
                .where(
                        apartmentTransaction.x.isNull(),
                        apartmentTransaction.y.isNull()
                ));
    }

    @Bean(name = STEP_NAME + " Processor")
    @StepScope
    public ItemProcessor<TransactionWithGu, ApartmentGeoRecord> kaKaoMapProcessor(KaKaoApiClientWithJibun kaKaoApiClient) {
        return kaKaoApiClient::callApi;
    }

    @Bean(name = STEP_NAME + " AsyncProcessor")
    @StepScope
    public AsyncItemProcessor<TransactionWithGu, ApartmentGeoRecord> asyncKaKaoMapProcessor() {
        AsyncItemProcessor<TransactionWithGu, ApartmentGeoRecord> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(kaKaoMapProcessor(kaKaoApiClient()));
        asyncItemProcessor.setTaskExecutor(taskExecutor);
        return asyncItemProcessor;
    }


    @Bean(name = STEP_NAME)
    public Step kaKaoMapStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<TransactionWithGu, Future<ApartmentGeoRecord>>chunk(chunkSize, platformTransactionManager)
                .reader(querydslPagingItemReader())
                .processor(asyncKaKaoMapProcessor())
                .writer(kaKaoMapWriter)
                .build();
    }

    @Bean(name = JOB_NAME)
    public Job kaKaoMapJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(kaKaoMapStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

}
