package com.example.comprehensivedegisn.batch.kakao_map.jibun;

import com.example.comprehensivedegisn.SkipCompileTest;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest
class KaKaoMapBatchWithJibunConfigurationTest extends SkipCompileTest {

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;
    @Autowired
    private JobExplorer jobExplorer;

    @Test
    public void readerTest() throws Exception {
        Job job = beanFactory.getBean("kakaoMapJobWithJibun", Job.class);
        jobLauncherTestUtils.setJob(job);
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer).getNextJobParameters(job).toJobParameters();
        jobLauncherTestUtils.launchJob(jobParameters);
    }
}