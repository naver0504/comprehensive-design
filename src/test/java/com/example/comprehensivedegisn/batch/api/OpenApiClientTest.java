package com.example.comprehensivedegisn.batch.api;

import com.example.comprehensivedegisn.api_client.OpenApiClient;
import com.example.comprehensivedegisn.api_client.OpenApiRequest;
import com.example.comprehensivedegisn.batch.open_api.OpenAPiProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@SpringBootTest
@EnableConfigurationProperties(OpenAPiProperties.class)
@ActiveProfiles("local")
class OpenApiClientTest  {

    @Autowired
    private OpenAPiProperties openAPiProperties;

    @Test
    void callApiTest() {
        RestTemplate restTemplate = new RestTemplate();
        OpenApiClient openApiClient = new OpenApiClient(openAPiProperties, restTemplate, 1000);
        Assertions.assertDoesNotThrow(() -> openApiClient.callApi(new OpenApiRequest("11680", 1, LocalDate.now().minusMonths(1))));

    }
}