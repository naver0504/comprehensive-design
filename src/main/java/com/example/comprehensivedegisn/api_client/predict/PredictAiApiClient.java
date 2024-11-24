package com.example.comprehensivedegisn.api_client.predict;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.api_client.ApiClient;
import com.example.comprehensivedegisn.dto.response.PredictAiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class PredictAiApiClient implements ApiClient<ApartmentTransaction, PredictAiResponse> {

    private final RestTemplate restTemplate;
    private final PredictAiProperties predictAiProperties;

    @Override
    public String createUrl(ApartmentTransaction apartmentTransaction) {
        return predictAiProperties.url();
    }

    @Override
    @Cacheable(value = "predictAi", key = "#apartmentTransaction.dongEntity.gu + ':' + #apartmentTransaction.dongEntity.dongName + ':' " +
            "+ #apartmentTransaction.areaForExclusiveUse + ':' + #apartmentTransaction.floor + ':' + #apartmentTransaction.buildYear")
    public PredictAiResponse callApi(ApartmentTransaction apartmentTransaction) {
        PredictAiRequest predictAiRequest = new PredictAiRequest(apartmentTransaction);
        return restTemplate.postForObject(createUrl(apartmentTransaction), predictAiRequest, PredictAiResponse.class);
    }

    @Getter
    private static class PredictAiRequest {
        private final Gu gu;
        private final String dongName;
        private final double exclusiveArea;
        private final int floor;
        private final int buildYear;

        private PredictAiRequest(ApartmentTransaction apartmentTransaction) {
            this.gu = apartmentTransaction.getDongEntity().getGu();
            this.dongName = apartmentTransaction.getDongEntity().getDongName();
            this.exclusiveArea = apartmentTransaction.getAreaForExclusiveUse();
            this.floor = apartmentTransaction.getFloor();
            this.buildYear = apartmentTransaction.getBuildYear();
        }

        @Override
        public String toString() {
            return gu +
                    ":" + dongName +
                    ":" + exclusiveArea +
                    ":" + floor +
                    ":" + buildYear;
        }
    }
}
