package com.example.comprehensivedegisn.api_client.predict;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.PredictCost;
import com.example.comprehensivedegisn.adapter.domain.PredictStatus;
import com.example.comprehensivedegisn.batch.predict_cost.ApartmentQueryRecord;
import com.example.comprehensivedegisn.dto.response.PredictCostResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class PredictApiClientForBatch extends PredictApiClient<ApartmentQueryRecord, PredictCost> {

    private final RestTemplate restTemplate;

    public PredictApiClientForBatch(PredictAiProperties predictAiProperties, RestTemplate restTemplate) {
        super(predictAiProperties);
        this.restTemplate = restTemplate;
    }

    @Override
    protected String createPath() {
        return predictAiProperties.predictCostPath();
    }

    @Override
    public String createUrl(ApartmentQueryRecord apartmentQueryRecord) {
        return super.createUrl(apartmentQueryRecord)
                + "&interestRate=" + apartmentQueryRecord.getInterestRate()
                + "&dealDate=" + apartmentQueryRecord.getDealDate()
                + "&dealAmount=" + apartmentQueryRecord.getDealAmount();
    }

    @Override
    public PredictCost callApi(ApartmentQueryRecord apartmentQueryRecord) {
        PredictCostResponse response = restTemplate.exchange(
                createUrl(apartmentQueryRecord),
                HttpMethod.GET,
                createHttpEntities(),
                PredictCostResponse.class
        ).getBody();

        return PredictCost.builder().predictedCost(response.prediction()).isReliable(response.reliable())
                .predictStatus(PredictStatus.RECENT).apartmentTransaction(apartmentQueryRecord).build();
    }
}
