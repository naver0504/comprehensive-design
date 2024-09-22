package com.example.comprehensivedegisn.batch.open_api.api;


import com.example.comprehensivedegisn.batch.open_api.api.dto.ApartmentDetailResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenAPiProperties.class)
@Slf4j
public class OpenApiClient {

    private final int NUM_OF_ROWS = 1000;
    private final OpenAPiProperties openAPiProperties;

    public ApartmentDetailResponse request(int pageNo, LocalDate contractDate, String regionalCode) {

        RestTemplate restTemplate = new RestTemplate();

        OpenApiRequest openApiRequest = OpenApiRequest.builder()
                .numOfRows(NUM_OF_ROWS)
                .LAWD_CD(regionalCode)
                .pageNo(pageNo)
                .contractDate(contractDate)
                .build();

        return restTemplate.getForObject(createOpenApiRequestUri(openApiRequest), ApartmentDetailResponse.class);
    }

    private URI createOpenApiRequestUri(OpenApiRequest openApiRequest) {
        StringBuilder urlBuilder = new StringBuilder(openAPiProperties.endPoint());
        urlBuilder.append("?").append("serviceKey=").append(openAPiProperties.serviceKey());
        urlBuilder.append("&").append("numOfRows=").append(URLEncoder.encode(String.valueOf(openApiRequest.getNumOfRows()), StandardCharsets.UTF_8));
        urlBuilder.append("&").append("LAWD_CD=").append(URLEncoder.encode(openApiRequest.getLAWD_CD(), StandardCharsets.UTF_8));
        urlBuilder.append("&").append("pageNo=").append(URLEncoder.encode(String.valueOf(openApiRequest.getPageNo()), StandardCharsets.UTF_8));
        urlBuilder.append("&").append("DEAL_YMD=").append(URLEncoder.encode(openApiRequest.getContractDate(), StandardCharsets.UTF_8));
        return URI.create(urlBuilder.toString());
    }

    @Getter
    private static class OpenApiRequest {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

        private final int numOfRows;
        private final String LAWD_CD;
        private final int pageNo;
        private final String contractDate;

        @Builder
        public OpenApiRequest(int numOfRows, String LAWD_CD, int pageNo, LocalDate contractDate) {
            this.numOfRows = numOfRows;
            this.LAWD_CD = LAWD_CD;
            this.pageNo = pageNo;
            this.contractDate = convertLocalDateToString(contractDate);
        }

        public String convertLocalDateToString(LocalDate localDate) {
            return formatter.format(localDate);
        }
    }
}
