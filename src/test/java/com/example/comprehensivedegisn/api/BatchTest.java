package com.example.comprehensivedegisn.api;

import com.example.comprehensivedegisn.batch.open_api.JdbcTemplateConfig;
import com.example.comprehensivedegisn.batch.open_api.OpenApiBatchConfiguration;
import com.example.comprehensivedegisn.api_client.OpenApiClient;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.repository.apart.ApartmentTransactionRepository;
import com.example.comprehensivedegisn.adapter.repository.dong.DongRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ComponentScan(basePackageClasses = {OpenApiBatchConfiguration.class, DongRepository.class})
@Import({OpenApiClient.class, TestBatchConfig.class, JdbcTemplateConfig.class})
@SpringBatchTest
@ActiveProfiles("local")
public class BatchTest {


    @Autowired
    private ApartmentTransactionRepository apartmentTransactionRepository;
    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private BeanFactory beanFactory;

    @AfterEach
    void tearDown() {
//        jobRepositoryTestUtils.removeJobExecutions();
//        apartmentTransactionRepository.deleteAll();
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
        jobParametersBuilder.addString("regionalCode", Gu.송파구.getRegionalCode());
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();

        jobLauncherTestUtils.setJob(job);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Check if the job execution is successful
        jobExecution.getExecutionContext().entrySet().forEach(System.out::println);
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    void testExcelJob() throws Exception {
        Job job = beanFactory.getBean("excelWriterJob", Job.class);

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("regionalCode", Gu.강남구.getRegionalCode());
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();

        jobLauncherTestUtils.setJob(job);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(job)
                .addJobParameters(jobParameters)
                .toJobParameters());

        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);    }
}
