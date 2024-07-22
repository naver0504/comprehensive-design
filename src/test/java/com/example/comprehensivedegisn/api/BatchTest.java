package com.example.comprehensivedegisn.api;

import com.example.comprehensivedegisn.batch.open_api.JdbcTemplateConfig;
import com.example.comprehensivedegisn.batch.open_api.OpenApiBatchConfiguration;
import com.example.comprehensivedegisn.domain.Gu;
import com.example.comprehensivedegisn.domain.repository.ApartmentTransactionRepository;
import com.example.comprehensivedegisn.domain.repository.DongRepository;
import org.aspectj.lang.annotation.After;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


import static com.example.comprehensivedegisn.domain.Gu.노원구;

@SpringBootTest
@ComponentScan(basePackageClasses = {OpenApiBatchConfiguration.class, DongRepository.class})
@Import({OpenApiClient.class, OpenApiUtils.class, TestBatchConfig.class, JdbcTemplateConfig.class})
@SpringBatchTest
public class BatchTest {


    @Autowired
    private ApartmentTransactionRepository apartmentTransactionRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private BeanFactory beanFactory;

    @AfterEach
    void tearDown() {
        jobRepositoryTestUtils.removeJobExecutions();
        apartmentTransactionRepository.deleteAll();
    }

    @Test
    void checkStep() throws Exception {

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("regionalCode", 노원구.getRegionalCode());

        JobInstance jobInstance = jobRepository.getJobInstance("simpleOpenApiJob",
                jobParametersBuilder.toJobParameters());

        StepExecution simpleOpenApiStep = jobRepository.getLastStepExecution(jobInstance, "simpleOpenApiStep");
        ExecutionContext executionContext = simpleOpenApiStep.getExecutionContext();

        Assertions.assertThat(executionContext.getInt("lastPageNo")).isNotNull();
        Assertions.assertThat(executionContext.getString("lastContractDate")).isNotNull();
    }

    @Test
    void testJob() throws Exception {


        Job job = beanFactory.getBean("simpleOpenApiJob", Job.class);

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("regionalCode", Gu.중구.getRegionalCode());
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();

        jobLauncherTestUtils.setJob(job);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Check if the job execution is successful
        jobExecution.getExecutionContext().entrySet().forEach(System.out::println);
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}
