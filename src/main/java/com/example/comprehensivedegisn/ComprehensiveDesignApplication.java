package com.example.comprehensivedegisn;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.*;

@SpringBootApplication
@EnableBatchProcessing
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class ComprehensiveDesignApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComprehensiveDesignApplication.class, args);
    }

}
