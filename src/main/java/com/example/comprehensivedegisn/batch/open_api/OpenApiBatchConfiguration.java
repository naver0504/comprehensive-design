package com.example.comprehensivedegisn.batch.open_api;

import com.example.comprehensivedegisn.batch.open_api.api.OpenApiClient;
import com.example.comprehensivedegisn.batch.open_api.api.dto.ApartmentDetailResponse;
import com.example.comprehensivedegisn.domain.repository.DongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;


@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
public class OpenApiBatchConfiguration {

    private final OpenApiClient openApiClient;
    private final PlatformTransactionManager platformTransactionManager;
    private final JdbcTemplate jdbcTemplate;
    private final DongRepository dongRepository;

    public static int CHUNK_SIZE = 1;

    @Bean(name = "simpleOpenApiJob")
    public Job simpleOpenApiJob(JobRepository jobRepository) {
        return new JobBuilder("simpleOpenApiJob", jobRepository)
                .start(simpleOpenApiStep(jobRepository, platformTransactionManager))
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step simpleOpenApiStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("simpleOpenApiStep", jobRepository)
                .<ApartmentDetailResponse, ApartmentDetailResponse>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(simpleOpenApiReader())
                .writer(openApiJdbcWriter())
                .build();
    }


    @Bean
    @JobScope
    public OpenApiDongDataHolder openApiDongDataHolder() {
        return new OpenApiDongDataHolder(dongRepository);
    }

    @Bean
    @StepScope
    public OpenApiBatchReader simpleOpenApiReader() {
        return new OpenApiBatchReader(openApiClient);
    }

    @Bean
    @StepScope
    public OpenApiJdbcWriter openApiJdbcWriter() {
        return new OpenApiJdbcWriter(openApiDongDataHolder(), jdbcTemplate);
    }
}
