package com.example.comprehensivedegisn.batch.open_api.all;

import com.example.comprehensivedegisn.api_client.open_api.OpenApiClient;
import com.example.comprehensivedegisn.batch.open_api.OpenApiBaseBatchConfiguration;
import com.example.comprehensivedegisn.batch.open_api.dto.ApartmentDetailResponseWithGu;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Import(OpenApiBaseBatchConfiguration.class)
public class OpenApiAllGuBatchConfiguration {

    private static final String JOB_NAME = "allGuOpenApiJob";
    private static final String STEP_NAME = JOB_NAME + "Step";
    private static final int CHUNK_SIZE = 1;
    private static final int NUM_OF_ROWS = 1000;

    private final OpenApiClient openApiClient;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean(name = JOB_NAME)
    public Job openApiAllGuJob(JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(openApiAllGuStep(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean(name = STEP_NAME)
    public Step openApiAllGuStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<ApartmentDetailResponseWithGu, ApartmentDetailResponseWithGu>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(openApiAllGuBatchReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    @StepScope
    public OpenApiAllGuBatchReader openApiAllGuBatchReader() {
        return new OpenApiAllGuBatchReader(openApiClient, NUM_OF_ROWS);
    }

    @Bean
    @StepScope
    public ItemWriter<ApartmentDetailResponseWithGu> itemWriter() {
        return (items) -> {
            for (ApartmentDetailResponseWithGu item : items) {
                System.out.println(item.apartmentDetailResponse().body().totalCount());
            }
        };
    }
}
