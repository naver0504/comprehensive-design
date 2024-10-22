package com.example.comprehensivedegisn.batch.open_api;

import com.example.comprehensivedegisn.batch.api_client.OpenApiClient;
import com.example.comprehensivedegisn.batch.open_api.dto.ApartmentDetailResponse;
import com.example.comprehensivedegisn.domain.repository.QuerydslDongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenAPiProperties.class)
public class OpenApiBatchConfiguration {

    private static final String JOB_NAME = "simpleOpenApiJob";
    private static final String STEP_NAME = JOB_NAME + "Step";
    private static final int CHUNK_SIZE = 1;
    private static final int NUM_OF_ROWS = 1000;

    private final OpenAPiProperties openAPiProperties;
    private final PlatformTransactionManager platformTransactionManager;
    private final QuerydslDongRepository querydslDongRepository;
    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;

    @Bean(name = JOB_NAME)
    public Job simpleOpenApiJob(JobRepository jobRepository) {
        return new JobBuilder("simpleOpenApiJob", jobRepository)
                .start(simpleOpenApiStep(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean(name = STEP_NAME)
    public Step simpleOpenApiStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("simpleOpenApiStep", jobRepository)
                .<ApartmentDetailResponse, ApartmentDetailResponse>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(simpleOpenApiReader())
                .writer(openApiJdbcWriter())
                .build();
    }

    @Bean(name = JOB_NAME + "numOfRows")
    public Integer numOfRows() {
        return NUM_OF_ROWS;
    }

    @Bean
    @JobScope
    public OpenApiDongDataHolder openApiDongDataHolder() {
        return new OpenApiDongDataHolder(querydslDongRepository);
    }

    @Bean
    @StepScope
    public OpenApiBatchReader simpleOpenApiReader() {
        return new OpenApiBatchReader(openApiClient(), numOfRows());
    }

    @Bean
    @StepScope
    public OpenApiClient openApiClient() {
        return new OpenApiClient(openAPiProperties, restTemplate, numOfRows());
    }

    @Bean
    @StepScope
    public OpenApiJdbcWriter openApiJdbcWriter() {
        return new OpenApiJdbcWriter(openApiDongDataHolder(), jdbcTemplate);
    }
}
