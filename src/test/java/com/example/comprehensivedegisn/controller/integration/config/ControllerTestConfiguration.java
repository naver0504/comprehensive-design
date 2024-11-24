package com.example.comprehensivedegisn.controller.integration.config;


import com.example.comprehensivedegisn.api_client.predict.PredictAiApiClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerTestConfiguration {
    @MockBean
    private PredictAiApiClient mockPredictAiApiClient;
}
