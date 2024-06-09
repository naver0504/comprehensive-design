package com.example.comprehensivedegisn.api;

import com.example.comprehensivedegisn.api.dto.ApartmentDetailRootElement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@EnableConfigurationProperties(OpenAPiProperties.class)
@Component
@RequiredArgsConstructor
public final class OpenApiUtils {

    private final OpenAPiProperties openAPiProperties;

    public URI createURI(OpenApiRequest openApiRequest) {
        StringBuilder urlBuilder = new StringBuilder(openAPiProperties.endPoint());
        urlBuilder.append("?").append("serviceKey=").append(URLEncoder.encode(openAPiProperties.serviceKey(), StandardCharsets.UTF_8));
        urlBuilder.append("&").append("numOfRows=").append(URLEncoder.encode(String.valueOf(openApiRequest.getNumOfRows()), StandardCharsets.UTF_8));
        urlBuilder.append("&").append("LAWD_CD=").append(URLEncoder.encode(openApiRequest.getLAWD_CD(), StandardCharsets.UTF_8));
        urlBuilder.append("&").append("pageNo=").append(URLEncoder.encode(String.valueOf(openApiRequest.getPageNo()), StandardCharsets.UTF_8));
        urlBuilder.append("&").append("DEAL_YMD=").append(URLEncoder.encode(openApiRequest.getFormattedContractDate(), StandardCharsets.UTF_8));
        return URI.create(urlBuilder.toString());
    }

    public ApartmentDetailRootElement convertXmlToApartmentDetail(String xml) throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xml, ApartmentDetailRootElement.class);
    }
}
