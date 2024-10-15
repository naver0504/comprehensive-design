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
        //URI.create()를 사용하지 않고 String Type을 넣으면 serviceKey에 특수문자가 들어가면 RestTemplate이 인코딩을 해버림
        return restTemplate.getForObject(URI.create(createUrl(openApiRequest)), ApartmentDetailResponse.class);
    }
}
