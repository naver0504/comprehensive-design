package com.example.comprehensivedegisn.api_client.predict;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.dto.response.PredictAiResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class PredictApiClientForGraph extends PredictApiClient<ApartmentTransaction, PredictAiResponse> {

    private final RestTemplate restTemplate;

    public PredictApiClientForGraph(RestTemplate restTemplate, PredictAiProperties predictAiProperties) {
        super(predictAiProperties);
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = "predictAi", key = "#apartmentTransaction.dongEntity.gu + ':' + #apartmentTransaction.dongEntity.dongName + ':' " +
            "+ #apartmentTransaction.areaForExclusiveUse + ':' + #apartmentTransaction.floor + ':' + #apartmentTransaction.buildYear")
    public PredictAiResponse callApi(ApartmentTransaction apartmentTransaction) {
        return new PredictAiResponse(restTemplate.exchange(
                createUrl(apartmentTransaction),
                HttpMethod.GET,
                createHttpEntities(),
                Map.class
        ).getBody());
    }

    @Override
    protected String createPath() {
        return predictAiProperties.predictGraphPath();
    }
}
