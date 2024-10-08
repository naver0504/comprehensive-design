package com.example.comprehensivedegisn.batch.kakao_map.api_client;

import org.springframework.http.HttpEntity;

public interface KaKaoApiClient<T, R> {

    String createUrl(String location);
    HttpEntity<String> createHttpEntity();
    R callApi(T t);
}
