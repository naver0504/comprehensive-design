package com.example.comprehensivedegisn.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open.api")
public record OpenAPiProperties(String endPoint, String serviceKey) {}
