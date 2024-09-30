package com.example.comprehensivedegisn.batch.kakao_map.api;

import com.example.comprehensivedegisn.batch.kakao_map.KaKaoRestApiProperties;
import com.example.comprehensivedegisn.batch.kakao_map.api.dto.ApartmentGeoRecord;
import com.example.comprehensivedegisn.batch.kakao_map.api.dto.Documents;
import com.example.comprehensivedegisn.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.domain.Gu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
public class KaKaoApiClient {

    @Value("#{jobParameters[regionalCode]}")
    private String regionalCode;

    private final String HEADER_PREFIX = "KakaoAK ";
    private final String QUERY_PREFIX = "서울 ";

    private final RestTemplate restTemplate;
    private final KaKaoRestApiProperties kaKaoRestApiProperties;


    public ApartmentGeoRecord getGeoLocation(ApartmentTransaction apartmentTransaction) {

        String roadName = createRoadName(apartmentTransaction);
        Documents documents = restTemplate.exchange(createUri(roadName), HttpMethod.GET, createHttpEntity(), Documents.class).getBody();
        if(!documents.isValid(roadName)) {
            log.error(roadName);
            log.error(documents.toString());
            throw new IllegalArgumentException("Invalid response from KaKao API");
        }
        return documents.toApartmentGeoRecord(apartmentTransaction);
    }

    public String createRoadName(ApartmentTransaction apartmentTransaction) {
        return QUERY_PREFIX + Gu.getGuFromRegionalCode(regionalCode) + " " + apartmentTransaction.getRoadName();
    }

    public String createUri(String roadName) {
        return kaKaoRestApiProperties.url() + "?query=" + roadName;
    }

    public HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", HEADER_PREFIX + kaKaoRestApiProperties.key());
        return new HttpEntity<>(headers);
    }
}
