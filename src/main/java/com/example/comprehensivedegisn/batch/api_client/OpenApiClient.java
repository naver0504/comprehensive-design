package com.example.comprehensivedegisn.batch.api_client;


import com.example.comprehensivedegisn.batch.open_api.OpenAPiProperties;
import com.example.comprehensivedegisn.batch.open_api.dto.ApartmentDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@RequiredArgsConstructor
@Slf4j
public class OpenApiClient implements ApiClient<OpenApiRequest, ApartmentDetailResponse> {

    private static final int NUM_OF_ROWS = 1000;
    private final OpenAPiProperties openAPiProperties;
    private final RestTemplate restTemplate;


    @Override
    public String createUrl(OpenApiRequest openApiRequest) {
        return openAPiProperties.endPoint() + "?" + "serviceKey=" + openAPiProperties.serviceKey() +
                "&" + "numOfRows=" + NUM_OF_ROWS +
                "&" + "LAWD_CD=" + openApiRequest.regionalCode() +
                "&" + "pageNo=" + openApiRequest.pageNo() +
                "&" + "DEAL_YMD=" + openApiRequest.contractDate();    }

    @Override
    public ApartmentDetailResponse callApi(OpenApiRequest openApiRequest) {
        return restTemplate.getForObject(URI.create(createUrl(openApiRequest)), ApartmentDetailResponse.class);
    }
}
