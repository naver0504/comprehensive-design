package com.example.comprehensivedegisn.api;


import com.example.comprehensivedegisn.api.dto.ApartmentDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Date;


@Component
@RequiredArgsConstructor
@Slf4j
public class OpenApiClient {

    private final OpenApiUtils openApiUtils;
    private final int NUM_OF_ROWS = 1000;

    public ApartmentDetailResponse request(int pageNo, LocalDate contractDate, String regionalCode) {

        log.info("Reading page: {}", pageNo);
        log.info("Reading contractDate: {}", contractDate);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(openApiUtils.createURI(OpenApiRequest.builder()
                .LAWD_CD(regionalCode)
                .numOfRows(NUM_OF_ROWS)
                .pageNo(pageNo)
                .contractDate(contractDate)
                .build()), ApartmentDetailResponse.class);
    }
}
