package com.example.comprehensivedegisn.controller;


import com.example.comprehensivedegisn.adapter.domain.Gu;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/excel/write")
public class ExcelWriteBatchController {

    private final JobLauncher jobLauncher;
    private final JobExplorer explorer;
    private final BeanFactory beanFactory;


    @GetMapping
    public void writeExcel(@RequestParam("Gu") Gu gu) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = beanFactory.getBean("excelWriterJob", Job.class);
        jobLauncher.run(job, new JobParametersBuilder(explorer)
                .getNextJobParameters(job)
                .addJobParameters(new JobParametersBuilder()
                        .addString("regionalCode", gu.getRegionalCode())
                        .toJobParameters()).toJobParameters());

    }
}
