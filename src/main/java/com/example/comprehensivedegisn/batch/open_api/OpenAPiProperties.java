package com.example.comprehensivedegisn.batch.open_api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open.api")
public record OpenAPiProperties(String endPoint, String serviceKey) {}
