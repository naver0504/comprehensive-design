package com.example.comprehensivedegisn.service.integration.config;

import com.example.comprehensivedegisn.adapter.repository.BaseRepositoryTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@BaseRepositoryTest
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Transactional
@ComponentScan(basePackages = {"com.example.comprehensivedegisn.service"})
@ActiveProfiles("test")
public @interface IntegrationTestForService {
}
