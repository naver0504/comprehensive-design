package com.example.comprehensivedegisn.api;


import com.example.comprehensivedegisn.api.dto.ApartmentDetailRootElement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;


@Component
@RequiredArgsConstructor
public class OpenApiClient {

    private final OpenApiUtils openApiUtils;

    public ApartmentDetailRootElement request(int pageNo, LocalDate contractDate) throws Exception {
        RestClient restClient = RestClient.create();

        OpenApiRequest openApiRequest = OpenApiRequest.builder()
                .pageNo(pageNo)
                .contractDate(contractDate)
                .build();

        return openApiUtils.convertXmlToApartmentDetail(restClient.get()
                .uri(openApiUtils.createURI(openApiRequest))
                .retrieve()
                .body(String.class));
    }
}
