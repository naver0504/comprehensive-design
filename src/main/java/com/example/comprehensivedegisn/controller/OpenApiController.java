package com.example.comprehensivedegisn.controller;

import com.example.comprehensivedegisn.domain.Gu;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class OpenApiController {

    private final BeanFactory beanFactory;
    private final JobLauncher jobLauncher;

    @PostMapping("/api/insert")
    public void insertApi(@RequestParam Gu gu) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = beanFactory.getBean("simpleOpenApiJob", Job.class);
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        JobParameters jobParameters = jobParametersBuilder.addString("regionalCode", gu.getRegionalCode()).toJobParameters();
        jobLauncher.run(job, jobParameters);
    }
}
