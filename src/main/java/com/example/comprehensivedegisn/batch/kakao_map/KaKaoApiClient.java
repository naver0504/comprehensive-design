package com.example.comprehensivedegisn.batch.kakao_map;

import com.example.comprehensivedegisn.batch.kakao_map.dto.ApartmentGeoRecord;
import com.example.comprehensivedegisn.batch.kakao_map.dto.Documents;
import com.example.comprehensivedegisn.batch.kakao_map.dto.RoadNameLocationRecord;
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
    private final RoadNameCacheRepository roadNameCacheRepository;
    private final KaKaoRestApiProperties kaKaoRestApiProperties;


    public ApartmentGeoRecord getGeoLocation(ApartmentTransaction apartmentTransaction) {

        RoadNameLocationRecord roadNameLocationRecord = roadNameCacheRepository.computeIfAbsent(apartmentTransaction.getRoadName(), roadNm -> {
            Documents documents = restTemplate.exchange(createUriWithRoadName(roadNm), HttpMethod.GET, createHttpEntity(), Documents.class).getBody();
            return documents.toRoadNameLocationRecord();
        });

        return roadNameLocationRecord.toApartmentGeoRecord(apartmentTransaction.getId());
    }



    public String createUriWithRoadName(String roadName) {
        return kaKaoRestApiProperties.url() + "?query="
                + QUERY_PREFIX + Gu.getGuFromRegionalCode(regionalCode) + " " + roadName;
    }

    public HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", HEADER_PREFIX + kaKaoRestApiProperties.key());
        return new HttpEntity<>(headers);
    }
}
