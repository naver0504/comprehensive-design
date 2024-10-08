package com.example.comprehensivedegisn.batch.kakao_map.api_client;

import com.example.comprehensivedegisn.batch.kakao_map.KaKaoRestApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
public abstract class AbstractKaKaoApiClient<T, R> implements KaKaoApiClient<T, R> {



    private final String HEADER_PREFIX = "KakaoAK ";
    private final String QUERY_PREFIX = "서울 ";
    private final KaKaoRestApiProperties kaKaoRestApiProperties;

    @Override
    public String createUrl(String location) {
        return kaKaoRestApiProperties.url() + "?query="
                + QUERY_PREFIX + " " + location;
    }

    @Override
    public HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", HEADER_PREFIX + kaKaoRestApiProperties.key());
        return new HttpEntity<>(headers);
    }
}
