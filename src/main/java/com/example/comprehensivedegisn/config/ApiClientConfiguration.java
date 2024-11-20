package com.example.comprehensivedegisn.config;

import com.example.comprehensivedegisn.api_client.predict.PredictAiApiClient;
import com.example.comprehensivedegisn.api_client.predict.PredictAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(PredictAiProperties.class)
public class ApiClientConfiguration {

    private final PredictAiProperties predictAiProperties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PredictAiApiClient predictAiApiClient(RestTemplate restTemplate) {
        return new PredictAiApiClient(restTemplate, predictAiProperties);
    }
}
