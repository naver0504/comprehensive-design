package com.example.comprehensivedegisn.batch.kakao_map.api;

import com.example.comprehensivedegisn.batch.kakao_map.KaKaoRestApiProperties;
import com.example.comprehensivedegisn.batch.kakao_map.RoadNameCacheRepository;
import com.example.comprehensivedegisn.batch.kakao_map.api.dto.ApartmentGeoRecord;
import com.example.comprehensivedegisn.batch.kakao_map.api.dto.Documents;
import com.example.comprehensivedegisn.batch.kakao_map.api.dto.RoadNameLocationRecord;
import com.example.comprehensivedegisn.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.domain.Gu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

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

        Optional<RoadNameLocationRecord> optionalLocationRecord = roadNameCacheRepository.findByRoadName(apartmentTransaction.getRoadName());
        if (optionalLocationRecord.isPresent()) {
            RoadNameLocationRecord locationRecord = optionalLocationRecord.get();
            return new ApartmentGeoRecord(apartmentTransaction.getId(), locationRecord.x(), locationRecord.y());
        }

        Documents documents = restTemplate.exchange(createUriWithRoadName(apartmentTransaction), HttpMethod.GET, createHttpEntity(), Documents.class).getBody();
        ApartmentGeoRecord apartmentGeoRecord = documents.toApartmentGeoRecord(apartmentTransaction);
        roadNameCacheRepository.save(apartmentTransaction.getRoadName(), new RoadNameLocationRecord(apartmentGeoRecord.x(), apartmentGeoRecord.y()));
        return apartmentGeoRecord;
    }



    public String createUriWithRoadName(ApartmentTransaction apartmentTransaction) {
        return kaKaoRestApiProperties.url() + "?query="
                + QUERY_PREFIX + Gu.getGuFromRegionalCode(regionalCode) + " " + apartmentTransaction.getRoadName();
    }

    public HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", HEADER_PREFIX + kaKaoRestApiProperties.key());
        return new HttpEntity<>(headers);
    }
}
