package com.example.comprehensivedegisn.batch.kakao_map;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao.rest-api")
public record KaKaoRestApiProperties(String key, String url) {
}
