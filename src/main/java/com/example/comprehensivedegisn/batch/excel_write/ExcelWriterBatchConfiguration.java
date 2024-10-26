package com.example.comprehensivedegisn.batch.excel_write;

import com.example.comprehensivedegisn.batch.excel_write.dto.ExcelTransactionRecord;
import com.example.comprehensivedegisn.batch.query_dsl.QuerydslNoOffsetIdPagingItemReader;
import com.example.comprehensivedegisn.batch.query_dsl.expression.Expression;
import com.example.comprehensivedegisn.batch.query_dsl.options.QuerydslNoOffsetNumberOptions;
import com.example.comprehensivedegisn.domain.Gu;
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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.example.comprehensivedegisn.domain.QApartmentTransaction.*;
import static com.example.comprehensivedegisn.domain.QDongEntity.*;

@Configuration
@RequiredArgsConstructor
public class ExcelWriterBatchConfiguration {

    private static final String JOB_NAME = "excelWriterJob";
    private static final String STEP_NAME = JOB_NAME + "_step";
    private static final int chunkSize = 1000;
    private static final String DELIMITER = "|";

    private final EntityManagerFactory emf;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    @StepScope
    public QuerydslNoOffsetIdPagingItemReader<ExcelTransactionRecord, Long> apartmentTransactionReader(@Value("#{jobParameters[regionalCode]}") String regionalCode) {

        QuerydslNoOffsetNumberOptions<ExcelTransactionRecord, Long> options =
                new QuerydslNoOffsetNumberOptions<>(apartmentTransaction.id, Expression.ASC);
        return new QuerydslNoOffsetIdPagingItemReader<>(emf, chunkSize, options, query -> query
                .select(Projections.constructor(ExcelTransactionRecord.class,
                        apartmentTransaction.dealAmount,
                        apartmentTransaction.buildYear,
                        apartmentTransaction.dealYear,
                        apartmentTransaction.dealMonth,
                        apartmentTransaction.dealDay,
                        apartmentTransaction.roadName,
                        apartmentTransaction.roadNameBonbun,
                        apartmentTransaction.roadNameBubun,
                        apartmentTransaction.apartmentName,
                        apartmentTransaction.areaForExclusiveUse,
                        apartmentTransaction.jibun,
                        apartmentTransaction.floor,
                        apartmentTransaction.geography,
                        dongEntity.gu,
                        dongEntity.dongName
                        ))
                .from(apartmentTransaction)
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.eq(dongEntity))
                .where(dongEntity.guCode.eq(regionalCode)));
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<ExcelTransactionRecord> apartmentTransactionExcelWriter(@Value("#{jobParameters[regionalCode]}") String regionalCode) {
        Field[] fields = ExcelTransactionRecord.class.getDeclaredFields();
        List<String> fieldList = Arrays.stream(fields).map(Field::getName).toList();
        String[] fieldNames = fieldList.toArray(new String[0]);
        FlatFileItemWriter<ExcelTransactionRecord> flatFileItemWriter = new FlatFileItemWriterBuilder<ExcelTransactionRecord>()
                .name("apartmentTransactionWriter")
                .resource(new FileSystemResource("C:\\Users\\qortm\\intelliJ\\comprehensive-degisn\\src\\main\\resources\\static" + "/apartment_transaction_" + Gu.getGuFromRegionalCode(regionalCode) + ".txt"))
                .delimited()
                .delimiter(DELIMITER)
                .names(fieldNames)
                .shouldDeleteIfExists(true)
                .build();

        flatFileItemWriter.setHeaderCallback(writer -> writer.write(String.join(DELIMITER, fieldNames)));
        return flatFileItemWriter;
    }

    @Bean
    public Job excelWriterJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(excelWriterStep())
                .build();
    }

    @Bean
    public Step excelWriterStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<ExcelTransactionRecord, ExcelTransactionRecord>chunk(chunkSize, platformTransactionManager)
                .reader(apartmentTransactionReader(null))
                .writer(apartmentTransactionExcelWriter(null))
                .build();
    }
}
