package com.example.comprehensivedegisn.batch.api_client;

import com.example.comprehensivedegisn.batch.CacheRepository;
import com.example.comprehensivedegisn.batch.kakao_map.KaKaoRestApiProperties;
import com.example.comprehensivedegisn.batch.kakao_map.dto.ApartmentGeoRecord;
import com.example.comprehensivedegisn.batch.kakao_map.dto.Documents;
import com.example.comprehensivedegisn.batch.kakao_map.dto.LocationRecord;
import com.example.comprehensivedegisn.batch.kakao_map.dto.TransactionWithGu;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class KaKaoApiClientWithJibun extends KaKaoApiClient<TransactionWithGu, ApartmentGeoRecord> {

    private final RestTemplate restTemplate;
    private final CacheRepository<String, LocationRecord> jibunCacheRepository;

    public KaKaoApiClientWithJibun(KaKaoRestApiProperties kaKaoRestApiProperties,
                                   RestTemplate restTemplate,
                                   CacheRepository<String, LocationRecord> jibunCacheRepository) {
        super(kaKaoRestApiProperties);
        this.restTemplate = restTemplate;
        this.jibunCacheRepository = jibunCacheRepository;
    }

    @Override
    public ApartmentGeoRecord callApi(TransactionWithGu transactionWithGu) {
        LocationRecord jibunLocationRecord = jibunCacheRepository.computeIfAbsent(
                transactionWithGu.getJibunAddress(),
                jibun -> {
                    if(jibun == null) return LocationRecord.EMPTY;
                    Documents documents = restTemplate
                            .exchange(
                                    createUrl(transactionWithGu),
                                    HttpMethod.GET,
                                    createHttpEntity(),
                                    Documents.class)
                            .getBody();
            return documents.toLocationRecord();
        });
        return jibunLocationRecord.toApartmentGeoRecord(transactionWithGu.id());    }

    @Override
    protected String getLocation(TransactionWithGu transactionWithGu) {
        return transactionWithGu.getJibunAddress();
    }
}
