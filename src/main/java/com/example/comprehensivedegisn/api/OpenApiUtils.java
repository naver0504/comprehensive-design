package com.example.comprehensivedegisn.api;

import com.example.comprehensivedegisn.api.dto.ApartmentDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@EnableConfigurationProperties(OpenAPiProperties.class)
@Component
@RequiredArgsConstructor
@Slf4j
public final class OpenApiUtils {

    private final OpenAPiProperties openAPiProperties;

    public static LocalDate getPreMonthContractDate(LocalDate localDate) {
        return localDate.minusMonths(1);
    }

    public static boolean isLimitExceeded(ApartmentDetailResponse response) {
        return response.isLimitExceeded();
    }

    public static boolean isEndOfData(ApartmentDetailResponse response) {
        return response.isEndOfData();
    }

    public URI createURI(OpenApiRequest openApiRequest) {
        StringBuilder urlBuilder = new StringBuilder(openAPiProperties.endPoint());
        urlBuilder.append("?").append("serviceKey=").append(openAPiProperties.serviceKey());
        urlBuilder.append("&").append("numOfRows=").append(URLEncoder.encode(String.valueOf(openApiRequest.getNumOfRows()), StandardCharsets.UTF_8));
        urlBuilder.append("&").append("LAWD_CD=").append(URLEncoder.encode(openApiRequest.getLAWD_CD(), StandardCharsets.UTF_8));
        urlBuilder.append("&").append("pageNo=").append(URLEncoder.encode(String.valueOf(openApiRequest.getPageNo()), StandardCharsets.UTF_8));
        urlBuilder.append("&").append("DEAL_YMD=").append(URLEncoder.encode(openApiRequest.getContractDate(), StandardCharsets.UTF_8));
        return URI.create(urlBuilder.toString());
    }

}
