package com.example.comprehensivedegisn.batch.api_client;

import com.example.comprehensivedegisn.batch.CacheRepository;
import com.example.comprehensivedegisn.batch.kakao_map.KaKaoRestApiProperties;
import com.example.comprehensivedegisn.batch.kakao_map.dto.ApartmentGeoRecord;
import com.example.comprehensivedegisn.batch.kakao_map.dto.Documents;
import com.example.comprehensivedegisn.batch.kakao_map.dto.LocationRecord;
import com.example.comprehensivedegisn.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.domain.Gu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class KaKaoApiClientWithRoadName extends KaKaoApiClient<ApartmentTransaction, ApartmentGeoRecord> {

    @Value("#{jobParameters[regionalCode]}")
    private String regionalCode;
    private final RestTemplate restTemplate;
    private final CacheRepository<String, LocationRecord> roadNameCacheRepository;

    public KaKaoApiClientWithRoadName(KaKaoRestApiProperties kaKaoRestApiProperties,
                                      RestTemplate restTemplate,
                                      CacheRepository<String, LocationRecord> roadNameCacheRepository) {
        super(kaKaoRestApiProperties);
        this.restTemplate = restTemplate;
        this.roadNameCacheRepository = roadNameCacheRepository;
    }

    @Override
    public ApartmentGeoRecord callApi(ApartmentTransaction apartmentTransaction) {

        LocationRecord roadNameLocationRecord = roadNameCacheRepository.computeIfAbsent(
                apartmentTransaction.getRoadName(),
                roadNm -> {
                    Documents documents = restTemplate
                            .exchange(
                                    createUrl(apartmentTransaction),
                                    HttpMethod.GET,
                                    createHttpEntity(),
                                    Documents.class)
                            .getBody();
                    return documents.toLocationRecord();
                });
        return roadNameLocationRecord.toApartmentGeoRecord(apartmentTransaction.getId());
    }

    @Override
    protected String getLocation(ApartmentTransaction apartmentTransaction) {
        return apartmentTransaction.getRoadNameWithGu(Gu.getGuFromRegionalCode(regionalCode));
    }
}
